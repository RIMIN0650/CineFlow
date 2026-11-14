package com.example.cineflow.queue;

import com.example.cineflow.queue.model.QueueDto;
import com.example.cineflow.screening.ScreeningRepository;
import com.example.cineflow.screening.model.Screening;
import com.example.cineflow.user.UserRepository;
import com.example.cineflow.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final StringRedisTemplate redisTemplate;

    @Qualifier("queueJoinScript")
    private final DefaultRedisScript<Long> queueJoinScript;

    @Qualifier("queueAdmissionScript")
    private final DefaultRedisScript<Long> queueAdmissionScript;

    @Qualifier("queueTokenValidationScript")
    private final DefaultRedisScript<Long> queueTokenValidationScript;

    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;

    private static final int MAX_ACRIVE_USERS = 10;
    private final long TOKEN_TTL_SECONDS = 300;


    private String getWaitingKey(Long screeningIdx) {
        return "queue:" + screeningIdx + ":waiting";
    }

    private String getSequenceKey(Long screeningIdx) {
        return "queue:" + screeningIdx + ":sequence";
    }

    private String getActiveKey(Long screeningIdx) {
        return "queue:" + screeningIdx + ":active";
    }

    private String getTokenKey(Long screeningIdx, Long userIdx) {
        return "queue:" + screeningIdx + ":token" + userIdx;
    }


    // 대기열 등록
    public QueueDto.QueueEnterResponse enterQueue(
            Long screeningIdx,
            String email
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "사용자를 찾을 수 없습니다."
                        )
                );

        var screening = screeningRepository.findById(screeningIdx)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "상영 정보를 찾을 수 없습니다."
                        )
                );

        if (!screening.getStartTime().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "이미 시작했거나 종료된 상영입니다."
            );
        }

        Long userIdx = user.getIdx();

        String waitingKey = getWaitingKey(screeningIdx);
        String sequenceKey = getSequenceKey(screeningIdx);

        // Lua Script 실행
        redisTemplate.execute(
                queueJoinScript,
                List.of(waitingKey, sequenceKey),
                userIdx.toString()
        );

        // 등록 후 현재 대기 순번 조회
        Long rank = redisTemplate.opsForZSet()
                .rank(waitingKey, userIdx.toString());

        Long totalWaiting = redisTemplate.opsForZSet()
                .zCard(waitingKey); // zCard : 전체 대기 인원 조회

        if (rank == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "대기열 등록에 실패하였습니다."
            );
        }

        return new QueueDto.QueueEnterResponse(
                screeningIdx,
                userIdx,
                rank + 1,
                totalWaiting
        );

    }


    // 대기 순번 조회
    public QueueDto.QueueStatusResponse getQueueStatus(
            Long screeningIdx,
            String email
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(()->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "사용자를 찾을 수 없습니다."
                        )
                );

        screeningRepository.findById(screeningIdx)
                .orElseThrow(()->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "상영 정보를 찾을 수 없습니다."
                        )
                );

        String waitingKey = getWaitingKey(screeningIdx);

        Long rank = redisTemplate.opsForZSet()
                .rank(waitingKey, user.getIdx().toString());

        Long totalWaiting = redisTemplate.opsForZSet()
                .zCard(waitingKey); // zCard : 전체 대기 인원 조회

        if (rank == null) {
            return new QueueDto.QueueStatusResponse(
                    screeningIdx,
                    user.getIdx(),
                    null,
                    totalWaiting == null ? 0L : totalWaiting,
                "NOT_IN_QUEUE"
            );
        }

        return new QueueDto.QueueStatusResponse(
                screeningIdx,
                user.getIdx(),
                rank + 1,
                totalWaiting,
                "WAITING"
        );
    }



    // 입장 처리 메서드 추가
    public QueueDto.QueueAdmissionResponse admitQueue(
            Long screeningIdx,
            String email
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "사용자를 찾을 수 없습니다."
                        )
                );

        screeningRepository.findById(screeningIdx).
                orElseThrow(()->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "상영 정보를 찾을 수 없습니다."
                        )
                );

        Long userIdx = user.getIdx();

        String userId = userIdx.toString();

        String waitingKey = getWaitingKey(screeningIdx);
        String activeKey = getActiveKey(screeningIdx);
        String tokenKey = getTokenKey(screeningIdx, userIdx);

        String token = UUID.randomUUID().toString();

        long now = Instant.now().getEpochSecond();
        long expiresAt = now + TOKEN_TTL_SECONDS;

        Long result = redisTemplate.execute(
                queueAdmissionScript,
                List.of(waitingKey, activeKey, tokenKey),
                userId,
                token,
                String.valueOf(now),
                String.valueOf(expiresAt),
                String.valueOf(MAX_ACRIVE_USERS),
                String.valueOf(TOKEN_TTL_SECONDS)
        );

        if (result == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "입장 처리에 실패했습니다."
            );
        }

        if (result == 0) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "현재 입장 가능한 인원이 초과되었습니다."
            );
        }

        if (result == 2) {
            String existingToken = redisTemplate.opsForValue()
                    .get(tokenKey);

            if (existingToken == null) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "입장 토큰을 확인할 수 없습니다."
                );
            }

            Long remainingTtl = redisTemplate.getExpire(
                    tokenKey,
                    TimeUnit.SECONDS
            );

            return new QueueDto.QueueAdmissionResponse(
                    screeningIdx,
                    userIdx,
                    existingToken,
                    "ALREADY ADMITTED",
                    remainingTtl
            );
        }

        if (result == 3) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "아직 입장 순서가 아닙니다."
            );
        }

        return new QueueDto.QueueAdmissionResponse(
                screeningIdx,
                userIdx,
                token,
                "ADMITTED",
                TOKEN_TTL_SECONDS
        );

    }





    // 토큰 검증 메서드
    public QueueDto.QueueTokenValidationResponse validateToken(
            Long screeningIdx,
            String email,
            String token
    ) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "입장 토큰이 필요합니다."
            );
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "사용자를 찾을 수 없습니다."
                        )
                );

        screeningRepository.findById(screeningIdx)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "상영 정보를 찾을 수 없습니다."
                        )
                );

        Long userIdx = user.getIdx();

        String activeKey = getActiveKey(screeningIdx);
        String tokenKey = getTokenKey(screeningIdx, userIdx);

        long now = Instant.now().getEpochSecond();

        Long result = redisTemplate.execute(
                queueTokenValidationScript,
                List.of(activeKey, tokenKey),
                userIdx.toString(),
                token,
                String.valueOf(now)
        );

        boolean valid = result != null && result == 1L;

        return new QueueDto.QueueTokenValidationResponse(
                screeningIdx,
                userIdx,
                valid,
                valid ? "VALID" : "INVALID"
        );

    }


}

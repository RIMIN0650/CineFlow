package com.example.cineflow.queue;

import com.example.cineflow.queue.model.QueueDto;
import com.example.cineflow.screening.ScreeningRepository;
import com.example.cineflow.screening.model.Screening;
import com.example.cineflow.user.UserRepository;
import com.example.cineflow.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> queueJoinScript;

    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;

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

    private String getWaitingKey(Long screeningIdx) {
        return "queue:" + screeningIdx + ":waiting";
    }

    private String getSequenceKey(Long screeningIdx) {
        return "queue:" + screeningIdx + ":sequence";
    }
    
}

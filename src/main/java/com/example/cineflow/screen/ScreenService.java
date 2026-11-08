package com.example.cineflow.screen;

import com.example.cineflow.screen.model.Screen;
import com.example.cineflow.screen.model.ScreenDto;
import com.example.cineflow.theater.TheaterRepository;
import com.example.cineflow.theater.model.Theater;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScreenService {

    private final ScreenRepository screenRepository;
    private final TheaterRepository theaterRepository;

    // 상영관 등록
    @Transactional
    public ScreenDto.ScreenResponse createScreen(ScreenDto.ScreenRequest request) {

        Theater theater = findTheater(request.theaterIdx());

        // 동일 영화관 내 상영관 이름 중복 확인
        validateDuplicateName(
                request.theaterIdx(),
                request.name()
        );

        Screen screen = new Screen(
                theater,
                request.name(),
                request.totalSeats()
        );

        // Screen은 Theater와 연관관계를 맺어야 하므로
        // DB에서 실제 Theater 객체를 조회한 다음 생성자에 전달해야 함.
        // 이렇게 해야 JPA가 외래 키 관계를 정상적으로 관리할 수 있다.
        // 또한 @Transactional 안에서 Theater를 조회하기 때문에 LAZY로 설정된 Theater도
        // 응답 DTO로 변환할 때 정상적으로 접근할 수 있음.


        Screen savedScreen = screenRepository.save(screen);

        return ScreenDto.ScreenResponse.from(savedScreen);
    }

    // 전체 상영관 조회
    public List<ScreenDto.ScreenResponse> getScreens() {

        List<ScreenDto.ScreenResponse> responses = new ArrayList<>();

        for (Screen screen : screenRepository.findAll()) {
            responses.add(ScreenDto.ScreenResponse.from(screen));
        }

        return responses;
    }

    // 상영관 상세 조회
    public ScreenDto.ScreenResponse getScreen(Long idx) {

        Screen screen = findScreen(idx);

        return ScreenDto.ScreenResponse.from(screen);
    }

    // 특정 영화관의 상영관 조회
    public List<ScreenDto.ScreenResponse> getScreensByTheater(
            Long theaterIdx
    ) {

        // 영화관 존재 여부 확인
        findTheater(theaterIdx);

        return screenRepository.findByTheater_Idx(theaterIdx)
                .stream()
                .map(ScreenDto.ScreenResponse::from)
                .toList();
    }

    // 상영관 수정
    @Transactional
    public ScreenDto.ScreenResponse updateScreen(
            Long idx,
            ScreenDto.ScreenRequest request
    ) {

        Screen screen = findScreen(idx);

        Theater theater = findTheater(request.theaterIdx());

        // 현재 상영관과 동일한 이름이라면 중복으로 처리하지 않음
        if (!screen.getTheater().getIdx().equals(request.theaterIdx())
                || !screen.getName().equals(request.name())) {

            validateDuplicateName(
                    request.theaterIdx(),
                    request.name()
            );
        }

        screen.update(
                theater,
                request.name(),
                request.totalSeats()
        );

        return ScreenDto.ScreenResponse.from(screen);
    }

    // 상영관 삭제
    @Transactional
    public void deleteScreen(Long idx) {

        Screen screen = findScreen(idx);

        screenRepository.delete(screen);
    }

    // 영화관 조회
    private Theater findTheater(Long idx) {

        return theaterRepository.findById(idx)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "존재하지 않는 영화관입니다."
                ));
    }

    // 상영관 조회
    private Screen findScreen(Long idx) {

        return screenRepository.findById(idx)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "존재하지 않는 상영관입니다."
                ));
    }

    // 상영관 이름 중복 검증
    private void validateDuplicateName(
            Long theaterIdx,
            String name
    ) {

        if (screenRepository.existsByTheater_IdxAndName(
                theaterIdx,
                name
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "해당 영화관에 이미 존재하는 상영관 이름입니다."
            );
        }
    }


}

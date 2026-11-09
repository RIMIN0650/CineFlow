package com.example.cineflow.screen;

import com.example.cineflow.screen.model.Screen;
import com.example.cineflow.screen.model.ScreenSeat;
import com.example.cineflow.screen.model.ScreenSeatDto;
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
public class ScreenSeatService {

    private final ScreenSeatRepository screenSeatRepository;
    private final ScreenRepository screenRepository;

    // 좌석 등록
    @Transactional
    public ScreenSeatDto.ScreenSeatResponse createSeat(
            Long screenIdx,
            ScreenSeatDto.ScreenSeatRequest request
    ) {

        Screen screen = findScreen(screenIdx);

        // 좌석 위치 중복 확인
        validateDuplicateSeat(
                screenIdx,
                request.seatRow(),
                request.seatNumber()
        );

        // 전체 좌석 수 초과 확인
        long seatCount =
                screenSeatRepository.countByScreen_Idx(screenIdx);

        if (seatCount >= screen.getTotalSeats()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "상영관의 최대 좌석 수를 초과했습니다."
            );
        }

        ScreenSeat seat = new ScreenSeat(
                screen,
                request.seatRow(),
                request.seatNumber(),
                request.seatType()
        );

        screen.addSeat(seat);

        ScreenSeat savedSeat = screenSeatRepository.save(seat);

        return ScreenSeatDto.ScreenSeatResponse.from(savedSeat);
    }

    // 특정 상영관의 좌석 전체 조회
    public List<ScreenSeatDto.ScreenSeatResponse> getSeats(Long screenIdx) {

        findScreen(screenIdx);

        List<ScreenSeat> seats = screenSeatRepository
                .findByScreen_IdxOrderBySeatRowAscSeatNumberAsc(screenIdx);

        List<ScreenSeatDto.ScreenSeatResponse> responses = new ArrayList<>();
        for (ScreenSeat seat : seats) {
            responses.add(ScreenSeatDto.ScreenSeatResponse.from(seat));
        }

        return responses;
    }

    // 좌석 상세 조회
    public ScreenSeatDto.ScreenSeatResponse getSeat(Long idx) {

        ScreenSeat seat = findSeat(idx);

        return ScreenSeatDto.ScreenSeatResponse.from(seat);
    }

    // 좌석 수정
    @Transactional
    public ScreenSeatDto.ScreenSeatResponse updateSeat(
            Long idx,
            ScreenSeatDto.ScreenSeatRequest request
    ) {

        ScreenSeat seat = findSeat(idx);

        // 좌석 위치 중복 확인
        if (!seat.getSeatRow().equals(request.seatRow())
                || !seat.getSeatNumber().equals(request.seatNumber())) {

            validateDuplicateSeat(
                    seat.getScreen().getIdx(),
                    request.seatRow(),
                    request.seatNumber()
            );
        }

        seat.update(
                request.seatRow(),
                request.seatNumber(),
                request.seatType()
        );

        return ScreenSeatDto.ScreenSeatResponse.from(seat);
    }

    // 좌석 삭제
    @Transactional
    public void deleteSeat(Long idx) {

        ScreenSeat seat = findSeat(idx);

        seat.getScreen().removeSeat(seat);

        screenSeatRepository.delete(seat);
    }

    // 상영관 조회
    private Screen findScreen(Long idx) {

        return screenRepository.findById(idx)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "존재하지 않는 상영관입니다."
                ));
    }

    // 좌석 조회
    private ScreenSeat findSeat(Long idx) {

        return screenSeatRepository.findById(idx)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "존재하지 않는 좌석입니다."
                ));
    }

    // 좌석 중복 검증
    private void validateDuplicateSeat(
            Long screenIdx,
            String seatRow,
            Integer seatNumber
    ) {

        if (screenSeatRepository.existsByScreen_IdxAndSeatRowAndSeatNumber(
                screenIdx,
                seatRow,
                seatNumber
        )) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 존재하는 좌석입니다."
            );
        }
    }

}

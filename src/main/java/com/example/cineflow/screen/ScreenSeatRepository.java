package com.example.cineflow.screen;

import com.example.cineflow.screen.model.ScreenSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScreenSeatRepository extends JpaRepository<ScreenSeat, Long> {

    // 특정 상영관의 좌석 조회
    List<ScreenSeat> findByScreen_IdxOrderBySeatRowAscSeatNumberAsc(
            Long screenIdx
    );

    // 특정 위치의 좌석 중복 확인
    boolean existsByScreen_IdxAndSeatRowAndSeatNumber(
            Long screenIdx,
            String seatRow,
            Integer seatNumber
    );

    // 특정 좌석을 제외한 중복 확인
    boolean existsByScreen_IdxAndSeatRowAndSeatNumberAndIdxNot(
            Long screenIdx,
            String seatRow,
            Integer seatNumber,
            Long idx
    );

    // 상영관에 등록된 좌석 수
    long countByScreen_Idx(Long screenIdx);
}

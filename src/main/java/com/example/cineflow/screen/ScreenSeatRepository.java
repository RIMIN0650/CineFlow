package com.example.cineflow.screen;

import com.example.cineflow.screen.model.ScreenSeat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
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


    // 동시성 제어를 위한 메서드 추가
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT s
    FROM ScreenSeat s
    WHERE s.idx IN :seatIdxs
    ORDER BY s.idx ASC
""")
    List<ScreenSeat> findAllByIdxInWithLock(
            @Param("seatIdxs") Collection<Long> seatIdxs
    );
    /*
    비관적 락 사용하는 이유

    두 사용자가 동시에 A1 좌석을 예매한다고 생각해 보자.
    1. 사용자 A가 A1을 조회한다.
    2. 사용자 B도 A1을 조회한다.
    3. 두 사용자 모두 아직 예약되지 않았다고 판단한다.
    4. 두 사용자가 동시에 예매를 생성한다.

    이런 상황을 Race Condition 이라고 한다.
    PESSIMISTIC_WRITE 는 좌석 행을 잠가서 한 트랜잭션이 작업을 끝날 때까지 다른 트랜잭션이 해당 좌석을 잠그지 못하게 한다.
    여러 좌석을 동시에 잠글 때는 ID 오름차순으로 잠가 교착상태 가능성도 줄이기로 함.
     */

}

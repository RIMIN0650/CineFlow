package com.example.cineflow.screening;

import com.example.cineflow.screening.model.Screening;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    @EntityGraph(attributePaths = {
            "movie",
            "screen",
            "screen.theater"
    })

    List<Screening> findAllByOrderByStartTimeAsc();

    @EntityGraph(attributePaths = {
            "movie",
            "screen",
            "screen.theater"
    })
    List<Screening> findByScreen_IdxOrderByStartTimeAsc(Long screenIdx);

    @EntityGraph(attributePaths = {
            "movie",
            "screen",
            "screen.theater"
    })
    Optional<Screening> findWithDetailsByIdx(Long idx);

    // 기존 상영 일정과 시간이 겹치는지 확인
    boolean existsByScreen_IdxAndStartTimeLessThanAndEndTimeGreaterThan(
            Long screenIdx,
            LocalDateTime newEndTime,
            LocalDateTime newStartTime
    );

    // 수정 시 자기 자신은 중복 검사에서 제외
    boolean existsByScreen_IdxAndStartTimeLessThanAndEndTimeGreaterThanAndIdxNot(
            Long screenIdx,
            LocalDateTime newEndTime,
            LocalDateTime newStartTime,
            Long idx
    );
}

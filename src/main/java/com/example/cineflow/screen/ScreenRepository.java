package com.example.cineflow.screen;

import com.example.cineflow.screen.model.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScreenRepository extends JpaRepository<Screen, Long> {

    // 특정 영화관의 상영관 조회
    List<Screen> findByTheater_Idx(Long theaterIdx);

    // 동일 영화관 내 상영관 이름 중복 확인
    boolean existsByTheater_IdxAndName(
            Long theaterIdx,
            String name
    );

}

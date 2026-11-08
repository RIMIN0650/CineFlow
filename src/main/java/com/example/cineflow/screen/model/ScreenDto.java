package com.example.cineflow.screen.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ScreenDto {

    public record ScreenRequest(

            @NotNull(message = "영화관 ID는 필수입니다.")
            @Positive(message = "올바른 영화관 ID를 입력해주세요.")
            Long theaterIdx,

            @NotBlank(message = "상영관 이름은 필수입니다.")
            @Size(max = 50)
            String name,

            @NotNull(message = "전체 좌석 수는 필수입니다.")
            @Positive(message = "좌석 수는 1개 이상이어야 합니다.")
            Integer totalSeats

    ) {}

    public record ScreenResponse(
            Long idx,
            Long theaterIdx,
            String theaterName,
            String name,
            Integer totalSeats,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {

        public static ScreenResponse from(Screen screen) {
            return new ScreenResponse(
                    screen.getIdx(),
                    screen.getTheater().getIdx(),
                    screen.getTheater().getName(),
                    screen.getName(),
                    screen.getTotalSeats(),
                    screen.getCreatedAt(),
                    screen.getUpdatedAt()
            );
        }
    }

}

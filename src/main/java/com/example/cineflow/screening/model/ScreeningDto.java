package com.example.cineflow.screening.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class ScreeningDto {

    public record ScreeningRequest(
            @NotNull
            Long movieIdx,

            @NotNull
            Long screenIdx,

            @NotNull
            LocalDateTime startTime,

            @NotNull
            @Positive
            Integer price
    ) {}

    public record ScreeningResponse(
            Long idx,
            Long movieIdx,
            String movieTitle,
            Long theaterIdx,
            String theaterName,
            Long screenIdx,
            String screenName,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Integer price,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {

        public static ScreeningResponse from(Screening screening) {

            return new ScreeningResponse(
                    screening.getIdx(),
                    screening.getMovie().getIdx(),
                    screening.getMovie().getTitle(),
                    screening.getScreen().getTheater().getIdx(),
                    screening.getScreen().getTheater().getName(),
                    screening.getScreen().getIdx(),
                    screening.getScreen().getName(),
                    screening.getStartTime(),
                    screening.getEndTime(),
                    screening.getPrice(),
                    screening.getCreatedAt(),
                    screening.getUpdatedAt()
            );
        }
    }
}

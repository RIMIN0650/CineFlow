package com.example.cineflow.catalog.movie.model;

import com.example.cineflow.catalog.movie.enums.MovieRating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class MovieDto {

    public record MovieRequest(

            @NotBlank(message = "영화 제목은 필수입니다.")
            @Size(max = 100)
            String title,

            @NotBlank(message = "영화 제목은 필수입니다.")
            String description,

            @NotBlank(message = "영화 장르는 필수입니다.")
            String genre,

            @NotNull(message = "상영 시간은 필수입니다.")
            @Positive(message = "상영 시간은 1분 이상이어야 합니다.")
            Integer runningTime,

            @NotNull(message = "관람 등급은 필수입니다.")
            MovieRating rating,

            @NotNull(message = "개봉일은 필수입니다.")
            LocalDate releaseDate,

            String posterUrl

    ) {}

    public record MovieResponse(
            Long idx,
            String title,
            String description,
            String genre,
            Integer runningTime,
            MovieRating rating,
            LocalDate releaseDate,
            String posterUrl
    ) {
        public static MovieResponse from(Movie movie) {
            return new MovieResponse(
                    movie.getIdx(),
                    movie.getTitle(),
                    movie.getDescription(),
                    movie.getGenre(),
                    movie.getRunningTime(),
                    movie.getRating(),
                    movie.getReleaseDate(),
                    movie.getPosterUrl()
            );
        }
    }


}

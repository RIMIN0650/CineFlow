package com.example.cineflow.theater.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class TheaterDto {

    public record TheaterRequest(

            @NotBlank(message = "영화관 이름은 필수입니다.")
            @Size(max = 100)
            String name,

            @NotBlank(message = "영화관 주소는 필수입니다.")
            @Size(max = 255)
            String address,

            @NotBlank(message = "영화관 연락처는 필수입니다.")
            @Size(max = 20)
            String phone

    ) {}


    public record TheaterResponse(
            Long idx,
            String name,
            String address,
            String phone,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static TheaterResponse from(Theater theater) {
            return new TheaterResponse(
                    theater.getIdx(),
                    theater.getName(),
                    theater.getAddress(),
                    theater.getPhone(),
                    theater.getCreatedAt(),
                    theater.getUpdatedAt()
            );
        }
    }

}

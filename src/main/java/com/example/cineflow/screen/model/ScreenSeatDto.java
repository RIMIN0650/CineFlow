package com.example.cineflow.screen.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ScreenSeatDto {

    public record ScreenSeatRequest(

            @NotBlank(message = "좌석 행은 필수입니다.")
            @Size(max = 5)
            String seatRow,

            @NotNull(message = "좌석 번호는 필수입니다.")
            @Positive(message = "좌석 번호는 1 이상이어야 합니다.")
            Integer seatNumber,

            @NotNull(message = "좌석 유형은 필수입니다.")
            SeatType seatType

    ) {}

    public record ScreenSeatResponse(

            Long idx,
            Long screenIdx,
            String seatRow,
            Integer seatNumber,
            SeatType seatType

    ) {

        public static ScreenSeatResponse from(ScreenSeat seat) {

            return new ScreenSeatResponse(
                    seat.getIdx(),
                    seat.getScreen().getIdx(),
                    seat.getSeatRow(),
                    seat.getSeatNumber(),
                    seat.getSeatType()
            );
        }
    }
}

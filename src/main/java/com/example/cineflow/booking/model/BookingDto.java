package com.example.cineflow.booking.model;

import com.example.cineflow.screen.model.ScreenSeat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDto {

    public record BookingRequest(
            @NotNull
            @Positive
            Long screeningIdx,

            @NotEmpty
            List<@NotNull @Positive Long> screenSeatIdxs
    ) {}

    public record BookingResponse(
            Long idx,
            Long userIdx,
            Long screeningIdx,
            String movieTitle,
            String theaterName,
            String screenName,
            LocalDateTime startTime,
            LocalDateTime endTime,
            List<SeatInfo> seats,
            Long totalPrice,
            BookingStatus status,
            LocalDateTime createdAt
    ) {
        public record SeatInfo (
                Long screenSeatIdx,
                String seatRow,
                Integer seatNumber,
                String seatType,
                Integer price
        ) { }

        public static BookingResponse from (Booking booking) {

            List<SeatInfo> seats = new ArrayList<>();

            for (BookingSeat bookingSeat : booking.getBookingSeats()) {
                ScreenSeat screenSeat = bookingSeat.getScreenSeat();
                seats.add(new SeatInfo(
                        screenSeat.getIdx(),
                        screenSeat.getSeatRow(),
                        screenSeat.getSeatNumber(),
                        screenSeat.getSeatType().name(),
                        bookingSeat.getPrice()
                ));
            }

            return new BookingResponse(
                    booking.getIdx(),
                    booking.getUser().getIdx(),
                    booking.getScreening().getIdx(),
                    booking.getScreening().getMovie().getTitle(),
                    booking.getScreening().getScreen().getTheater().getName(),
                    booking.getScreening().getScreen().getName(),
                    booking.getScreening().getStartTime(),
                    booking.getScreening().getEndTime(),
                    seats,
                    booking.getTotalPrice(),
                    booking.getStatus(),
                    booking.getCreatedAt()
            );

        }


    }

}

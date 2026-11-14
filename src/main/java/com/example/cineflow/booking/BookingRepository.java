package com.example.cineflow.booking;

import com.example.cineflow.booking.model.Booking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {
            "user",
            "screening.movie",
            "screening.screen.theater",
            "bookingSeats.screenSeat"
    })
    List<Booking> findByUser_IdxOrderByCreatedAtDesc(Long userIdx);

    @EntityGraph(attributePaths = {
            "user",
            "screening.movie",
            "screening.screen.theater",
            "bookingSeats.screenSeat"
    })
    Optional<Booking> findWithDetailsByIdx(Long idx);
}

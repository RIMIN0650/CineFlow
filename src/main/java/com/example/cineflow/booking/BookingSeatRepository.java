package com.example.cineflow.booking;

import com.example.cineflow.booking.model.BookingSeat;
import com.example.cineflow.booking.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

    @Query("""
        SELECT COUNT(bs) > 0
        FROM BookingSeat bs
        WHERE bs.screening.idx = :screeningIdx
          AND bs.screenSeat.idx = :screenSeatIdx
          AND bs.booking.status IN :statuses
    """)
    boolean existsActiveBooking(
            @Param("screeningIdx") Long screeningIdx,
            @Param("screenSeatIdx") Long screenSeatIdx,
            @Param("statuses") Collection<BookingStatus> statuses
    );
}

package com.example.cineflow.booking.model;

import com.example.cineflow.screen.model.ScreenSeat;
import com.example.cineflow.screening.model.Screening;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_idx", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_idx", nullable = false)
    private Screening screening;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_seat_idx", nullable = false)
    private ScreenSeat screenSeat;

    @Column(nullable = false)
    private Integer price;

    public BookingSeat(
            Booking booking,
            Screening screening,
            ScreenSeat screenSeat,
            Integer price
    ) {
        this.booking = booking;
        this.screening = screening;
        this.screenSeat = screenSeat;
        this.price = price;
    }

}

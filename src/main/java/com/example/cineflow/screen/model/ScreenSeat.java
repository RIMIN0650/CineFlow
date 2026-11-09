package com.example.cineflow.screen.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
        // 같은 상영관에 A1 좌석이 두 개 만들어 지는 것을 방지
        // 하지만 다른 상영관에서는 A1 좌석이 존재해도 괜찮음.
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_screen_seat_position",
                        columnNames = {
                            "screen_idx",
                            "seat_row",
                            "seat_number"
                        }
                )
        }
)
public class ScreenSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY) // 여러 좌석이 하나의 상영관에 소속되기 때문에 사용하는 관계
    @JoinColumn(name = "screen_idx", nullable = false)
    private Screen screen;

    @Column(name = "seat_row", nullable = false, length = 5)
    private String seatRow;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType;

    public ScreenSeat(
            Screen screen,
            String seatRow,
            Integer seatNumber,
            SeatType seatType
    ) {
        this.screen = screen;
        this.seatRow = seatRow;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
    }

    public void update(
            String seatRow,
            Integer seatNumber,
            SeatType seatType
    ) {
        this.seatRow = seatRow;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
    }

}

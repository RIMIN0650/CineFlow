package com.example.cineflow.screen.model;

import com.example.cineflow.theater.model.Theater;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_screen_theater_name",
                        columnNames = {"theater_idx", "name"}
                )
        }
)
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_idx", nullable = false)
    private Theater theater;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Screen(
            Theater theater,
            String name,
            Integer totalSeats
    ) {
        this.theater = theater;
        this.name = name;
        this.totalSeats = totalSeats;
        this.createdAt = LocalDateTime.now();
    }

    public void update(
            Theater theater,
            String name,
            Integer totalSeats
    ) {
        this.theater = theater;
        this.name = name;
        this.totalSeats = totalSeats;
        this.updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "screen", fetch = FetchType.LAZY) // mappedBy : 외래키를 직접 관리하지 않는다는 의미.
    // 실제 외래 키는 ScreenSeat의 @ManyToOne이 관리
    private List<ScreenSeat> seats = new ArrayList<>();

    public void addSeat(ScreenSeat seat) {
        seats.add(seat);
    }

    public void removeSeat(ScreenSeat seat) {
        seats.remove(seat);
    }

}

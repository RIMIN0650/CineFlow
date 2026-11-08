package com.example.cineflow.screen.model;

import com.example.cineflow.theater.model.Theater;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

}

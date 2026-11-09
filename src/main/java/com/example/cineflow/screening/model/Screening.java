package com.example.cineflow.screening.model;

import com.example.cineflow.catalog.movie.model.Movie;
import com.example.cineflow.screen.model.Screen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(
        indexes = {
                @Index(
                        name = "idx_screening_screen_start_time",
                        columnList = "screen_idx, start_time"
                ),
                @Index(
                        name = "idx_screening_movie_start_time",
                        columnList = "movie_idx, start_time"
                )
        }
)
public class Screening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_idx", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_idx", nullable = false)
    private Screen screen;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Screening(
            Movie movie,
            Screen screen,
            LocalDateTime startTime,
            Integer price
    ) {
        this.movie = movie;
        this.screen = screen;
        this.startTime = startTime;
        this.endTime = calculateEndTime(movie, startTime);
        this.price = price;
    }

    public void update(
            Movie movie,
            Screen screen,
            LocalDateTime startTime,
            Integer price
    ) {
        this.movie = movie;
        this.screen = screen;
        this.startTime = startTime;
        this.endTime = calculateEndTime(movie, startTime);
        this.price = price;
    }

    private LocalDateTime calculateEndTime(
            Movie movie,
            LocalDateTime startTime
    ) {
        return startTime.plusMinutes(movie.getRunningTime());
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}

package com.example.cineflow.catalog.movie.model;

import com.example.cineflow.catalog.movie.enums.MovieRating;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    private String genre;

    @Column(nullable = false)
    private Integer runningTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovieRating rating;

    @Column(nullable = false)
    private LocalDate releaseDate;

    private String posterUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Movie(
            String title,
            String description,
            String genre,
            Integer runningTime,
            MovieRating rating,
            LocalDate releaseDate,
            String posterUrl
    ) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.runningTime = runningTime;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.createdAt = LocalDateTime.now();
        this.posterUrl = posterUrl;
    }

    public void update(
            String title,
            String description,
            String genre,
            Integer runningTime,
            MovieRating rating,
            LocalDate releaseDate,
            String posterUrl
    ) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.runningTime = runningTime;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.posterUrl = posterUrl;
        this.updatedAt = LocalDateTime.now();
        this.posterUrl = posterUrl;
    }



}

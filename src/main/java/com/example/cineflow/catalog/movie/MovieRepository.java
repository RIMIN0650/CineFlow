package com.example.cineflow.catalog.movie;

import com.example.cineflow.catalog.movie.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}

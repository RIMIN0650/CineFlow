package com.example.cineflow.theater;

import com.example.cineflow.theater.model.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterRepository extends JpaRepository<Theater, Long> {
}

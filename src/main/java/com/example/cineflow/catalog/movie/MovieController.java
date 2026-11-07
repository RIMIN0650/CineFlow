package com.example.cineflow.catalog.movie;

import com.example.cineflow.catalog.movie.model.MovieDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/catalog/movies")
public class MovieController {

    private final MovieService movieService;

    // 영화 등록
    @PostMapping
    public ResponseEntity<MovieDto.MovieResponse> createMovie(
            @Valid @RequestBody MovieDto.MovieRequest request
    ) {

        MovieDto.MovieResponse response = movieService.createMovie(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

    }

    // 영화 전체 조회
    @GetMapping
    public ResponseEntity<List<MovieDto.MovieResponse>> getMovie() {
        return ResponseEntity.ok(movieService.getMovies());
    }

    // 영화 상세 조회
    @GetMapping("/{idx}")
    public ResponseEntity<MovieDto.MovieResponse> getMovie(
            @PathVariable Long idx
    ) {
        return ResponseEntity.ok(movieService.getMovie(idx));
    }

    // 영화 수정
    @PutMapping("/{idx}")
    public ResponseEntity<MovieDto.MovieResponse> updateMovie(
            @PathVariable Long idx,
            @Valid @RequestBody MovieDto.MovieRequest request
    ) {
        return ResponseEntity.ok(
                movieService.updateMovie(idx, request)
        );
    }

    // 영화 삭제
    @DeleteMapping("/{idx}")
    public ResponseEntity<Void> deleteMovie(
            @PathVariable Long idx
    ) {
        movieService.deleteMovie(idx);

        return ResponseEntity.noContent().build();
    }

}

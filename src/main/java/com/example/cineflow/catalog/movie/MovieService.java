package com.example.cineflow.catalog.movie;


import com.example.cineflow.catalog.movie.model.Movie;
import com.example.cineflow.catalog.movie.model.MovieDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 조회 작업은 읽기 전용 트랜잭션으로 실행하도록 설정
public class MovieService {

    private final MovieRepository movieRepository;

    // 영화 등록
    @Transactional
    public MovieDto.MovieResponse createMovie(MovieDto.MovieRequest request) {
        Movie movie = new Movie(
                request.title(),
                request.description(),
                request.genre(),
                request.runningTime(),
                request.rating(),
                request.releaseDate(),
                request.posterUrl()
        );


        Movie savedMovie = movieRepository.save(movie);

        return MovieDto.MovieResponse.from(savedMovie);

    }


    // 영화 전체 조회
    public List<MovieDto.MovieResponse> getMovies() {

        List<Movie> movies = movieRepository.findAll();
        List<MovieDto.MovieResponse> responses = new ArrayList<>();

        for (Movie movie : movies) {
            responses.add(MovieDto.MovieResponse.from(movie));
        }

        return responses;
    }

    // 영화 상세 조회
    public MovieDto.MovieResponse getMovie(Long idx) {
        Movie movie = findMovie(idx);

        return MovieDto.MovieResponse.from(movie);
    }


    // 영화 수정
    @Transactional
    public MovieDto.MovieResponse updateMovie(Long idx, MovieDto.MovieRequest request) {

        Movie movie = findMovie(idx);

        movie.update(
                request.title(),
                request.description(),
                request.genre(),
                request.runningTime(),
                request.rating(),
                request.releaseDate(),
                request.posterUrl()
        );

        /*
        movieRepository.save()를 호출하지 않아도 수정되는 이유
        1. findMovie()로 Entity를 조회
        2. JPA 영속성 컨텍스트가 Entity를 관리함
        3. movie.update()로 Entity의 값을 변경한다.
        4. 트랜잭션이 종료될 때 변경 사항을 감지한다.
        5. 변경된 내용을 UPDATE SQL 로 실행한다.

        이것을 변경 감지라고 한다.
        */

        System.out.println(movie.getPosterUrl());

        return MovieDto.MovieResponse.from(movie);
    }

    // 영화 삭제
    @Transactional
    public void deleteMovie(Long idx) {
        Movie movie = findMovie(idx);

        movieRepository.delete(movie);
    }



    // 영화 조회 공통 메서드
    private Movie findMovie(Long idx) {
        return movieRepository.findById(idx)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "존재하지 않는 영화입니다."
                ));
    }
}
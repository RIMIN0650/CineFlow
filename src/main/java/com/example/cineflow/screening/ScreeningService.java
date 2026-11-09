package com.example.cineflow.screening;

import com.example.cineflow.catalog.movie.MovieRepository;
import com.example.cineflow.catalog.movie.model.Movie;
import com.example.cineflow.screen.ScreenRepository;
import com.example.cineflow.screen.model.Screen;
import com.example.cineflow.screening.model.Screening;
import com.example.cineflow.screening.model.ScreeningDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;

    // 상영 일정 등록
    @Transactional
    public ScreeningDto.ScreeningResponse createScreening(ScreeningDto.ScreeningRequest request) {

        Movie movie = findMovie(request.movieIdx());
        Screen screen = findScreen(request.screenIdx());

        LocalDateTime endTime =
                request.startTime().plusMinutes(movie.getRunningTime());

        validateScreeningTime(
                screen.getIdx(),
                request.startTime(),
                endTime
        );

        Screening screening = new Screening(
                movie,
                screen,
                request.startTime(),
                request.price()
        );

        Screening saved = screeningRepository.save(screening);

        return ScreeningDto.ScreeningResponse.from(saved);
    }

    // 전체 상영 일정 조회
    public List<ScreeningDto.ScreeningResponse> getScreenings() {

        List<Screening> screenings = screeningRepository.findAllByOrderByStartTimeAsc();

        List<ScreeningDto.ScreeningResponse> responses = new ArrayList<>();
        for (Screening screening : screenings) {
            responses.add(ScreeningDto.ScreeningResponse.from(screening));
        }

        return responses;
    }

    // 특정 상영관의 일정 조회
    public List<ScreeningDto.ScreeningResponse> getScreeningsByScreen(Long screenIdx) {

        findScreen(screenIdx);

        List<Screening> screenings = screeningRepository
                .findByScreen_IdxOrderByStartTimeAsc(screenIdx);

        List<ScreeningDto.ScreeningResponse> responses = new ArrayList<>();
        for (Screening screening : screenings) {
            responses.add(ScreeningDto.ScreeningResponse.from(screening));
        }

        return responses;
    }

    // 단건 조회
    public ScreeningDto.ScreeningResponse getScreening(Long idx) {

        Screening screening = findScreening(idx);

        return ScreeningDto.ScreeningResponse.from(screening);
    }

    // 상영 일정 수정
    @Transactional
    public ScreeningDto.ScreeningResponse updateScreening(
            Long idx,
            ScreeningDto.ScreeningRequest request
    ) {

        Screening screening = findScreening(idx);

        Movie movie = findMovie(request.movieIdx());
        Screen screen = findScreen(request.screenIdx());

        LocalDateTime endTime =
                request.startTime().plusMinutes(movie.getRunningTime());

        validateScreeningTimeForUpdate(
                screen.getIdx(),
                request.startTime(),
                endTime,
                idx
        );

        screening.update(
                movie,
                screen,
                request.startTime(),
                request.price()
        );

        return ScreeningDto.ScreeningResponse.from(screening);
    }

    // 상영 일정 삭제
    @Transactional
    public void deleteScreening(Long idx) {

        Screening screening = findScreening(idx);

        screeningRepository.delete(screening);
    }

    private void validateScreeningTime(
            Long screenIdx,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {

        boolean duplicated =
                screeningRepository
                        .existsByScreen_IdxAndStartTimeLessThanAndEndTimeGreaterThan(
                                screenIdx,
                                endTime,
                                startTime
                        );

        if (duplicated) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "해당 상영관에 이미 등록된 상영 일정과 시간이 겹칩니다."
            );
        }
    }

    private void validateScreeningTimeForUpdate(
            Long screenIdx,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long idx
    ) {

        boolean duplicated =
                screeningRepository
                        .existsByScreen_IdxAndStartTimeLessThanAndEndTimeGreaterThanAndIdxNot(
                                screenIdx,
                                endTime,
                                startTime,
                                idx
                        );

        if (duplicated) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "해당 상영관에 이미 등록된 상영 일정과 시간이 겹칩니다."
            );
        }
    }

    private Movie findMovie(Long idx) {

        return movieRepository.findById(idx)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "존재하지 않는 영화입니다."
                ));
    }

    private Screen findScreen(Long idx) {

        return screenRepository.findById(idx)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "존재하지 않는 상영관입니다."
                ));
    }

    private Screening findScreening(Long idx) {

        return screeningRepository.findWithDetailsByIdx(idx)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "존재하지 않는 상영 일정입니다."
                ));
    }

}

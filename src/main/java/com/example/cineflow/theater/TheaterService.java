package com.example.cineflow.theater;

import com.example.cineflow.theater.model.Theater;
import com.example.cineflow.theater.model.TheaterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TheaterService {

    private final TheaterRepository theaterRepository;

    // 영화관 등록
    @Transactional
    public TheaterDto.TheaterResponse createTheater(TheaterDto.TheaterRequest request) {

        Theater theater = new Theater(
                request.name(),
                request.address(),
                request.phone()
        );

        Theater savedTheater = theaterRepository.save(theater);

        return TheaterDto.TheaterResponse.from(savedTheater);
    }


    // 영화관 전체 조회
    public List<TheaterDto.TheaterResponse> getTheaters() {
        List<TheaterDto.TheaterResponse> responses = new ArrayList<>();

        for (Theater theater : theaterRepository.findAll()) {
            responses.add(TheaterDto.TheaterResponse.from(theater));
        }

        return responses;
    }


    // 영화관 상세 조회
    public TheaterDto.TheaterResponse getTheater(Long idx) {

        Theater theater = findTheater(idx);

        return TheaterDto.TheaterResponse.from(theater);
    }


    // 영화관 수정
    @Transactional
    public TheaterDto.TheaterResponse updateTheater(
            Long idx,
            TheaterDto.TheaterRequest request
    ) {
        Theater theater = findTheater(idx);

        theater.update(
                request.name(),
                request.address(),
                request.phone()
        );

        return TheaterDto.TheaterResponse.from(theater);
    }


    // 영화관 삭제
    @Transactional
    public void deleteTheater(long idx) {

        Theater theater = findTheater(idx);

        theaterRepository.delete(theater);
    }


    // 공통 조회 메서드
    private Theater findTheater(Long idx) {
        return theaterRepository.findById(idx)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "존재하지 않는 영화관입니다."
                ));
    }

}

package com.example.cineflow.screening;

import com.example.cineflow.screening.model.ScreeningDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/catalog/screenings")
public class ScreeningController {

    private final ScreeningService screeningService;

    // 상영 일정 등록
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScreeningDto.ScreeningResponse createScreening(
            @Valid @RequestBody ScreeningDto.ScreeningRequest request
    ) {
        return screeningService.createScreening(request);
    }

    // 전체 상영 일정 조회
    @GetMapping
    public List<ScreeningDto.ScreeningResponse> getScreenings() {

        return screeningService.getScreenings();
    }

    // 특정 상영관의 상영 일정 조회
    @GetMapping("/screen/{screenIdx}")
    public List<ScreeningDto.ScreeningResponse> getScreeningsByScreen(
            @PathVariable Long screenIdx
    ) {
        return screeningService.getScreeningsByScreen(screenIdx);
    }

    // 상영 일정 단건 조회
    @GetMapping("/{idx}")
    public ScreeningDto.ScreeningResponse getScreening(
            @PathVariable Long idx
    ) {
        return screeningService.getScreening(idx);
    }

    // 상영 일정 수정
    @PutMapping("/{idx}")
    public ScreeningDto.ScreeningResponse updateScreening(
            @PathVariable Long idx,
            @Valid @RequestBody ScreeningDto.ScreeningRequest request
    ) {
        return screeningService.updateScreening(idx, request);
    }

    // 상영 일정 삭제
    @DeleteMapping("/{idx}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteScreening(@PathVariable Long idx) {

        screeningService.deleteScreening(idx);
    }

}

package com.example.cineflow.screen;

import com.example.cineflow.screen.model.ScreenDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/catalog/screens")
public class ScreenController {

    private final ScreenService screenService;

    // 상영관 등록
    @PostMapping
    public ResponseEntity<ScreenDto.ScreenResponse> createScreen(
            @Valid @RequestBody ScreenDto.ScreenRequest request
    ) {

        ScreenDto.ScreenResponse response =
                screenService.createScreen(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 전체 상영관 조회
    @GetMapping
    public ResponseEntity<List<ScreenDto.ScreenResponse>> getScreens() {

        return ResponseEntity.ok(
                screenService.getScreens()
        );
    }

    // 상영관 상세 조회
    @GetMapping("/{idx}")
    public ResponseEntity<ScreenDto.ScreenResponse> getScreen(
            @PathVariable Long idx
    ) {

        return ResponseEntity.ok(
                screenService.getScreen(idx)
        );
    }

    // 특정 영화관의 상영관 조회
    @GetMapping("/theater/{theaterIdx}")
    public ResponseEntity<List<ScreenDto.ScreenResponse>> getScreensByTheater(
            @PathVariable Long theaterIdx
    ) {

        return ResponseEntity.ok(
                screenService.getScreensByTheater(theaterIdx)
        );
    }

    // 상영관 수정
    @PutMapping("/{idx}")
    public ResponseEntity<ScreenDto.ScreenResponse> updateScreen(
            @PathVariable Long idx,
            @Valid @RequestBody ScreenDto.ScreenRequest request
    ) {

        return ResponseEntity.ok(
                screenService.updateScreen(idx, request)
        );
    }

    // 상영관 삭제
    @DeleteMapping("/{idx}")
    public ResponseEntity<Void> deleteScreen(
            @PathVariable Long idx
    ) {

        screenService.deleteScreen(idx);

        return ResponseEntity.noContent().build();
    }

}

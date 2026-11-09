package com.example.cineflow.screen;

import com.example.cineflow.screen.model.ScreenSeatDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScreenSeatController {

    private final ScreenSeatService screenSeatService;

    // 좌석 등록
    @PostMapping("/api/catalog/screens/{screenIdx}/seats")
    public ResponseEntity<ScreenSeatDto.ScreenSeatResponse> createSeat(
            @PathVariable Long screenIdx,
            @Valid @RequestBody ScreenSeatDto.ScreenSeatRequest request
    ) {

        ScreenSeatDto.ScreenSeatResponse response =
                screenSeatService.createSeat(screenIdx, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 특정 상영관의 좌석 조회
    @GetMapping("/api/catalog/screens/{screenIdx}/seats")
    public ResponseEntity<List<ScreenSeatDto.ScreenSeatResponse>> getSeats(
            @PathVariable Long screenIdx
    ) {

        return ResponseEntity.ok(
                screenSeatService.getSeats(screenIdx)
        );
    }

    // 좌석 상세 조회
    @GetMapping("/api/catalog/seats/{idx}")
    public ResponseEntity<ScreenSeatDto.ScreenSeatResponse> getSeat(
            @PathVariable Long idx
    ) {

        return ResponseEntity.ok(
                screenSeatService.getSeat(idx)
        );
    }

    // 좌석 수정
    @PutMapping("/api/catalog/seats/{idx}")
    public ResponseEntity<ScreenSeatDto.ScreenSeatResponse> updateSeat(
            @PathVariable Long idx,
            @Valid @RequestBody ScreenSeatDto.ScreenSeatRequest request
    ) {

        return ResponseEntity.ok(
                screenSeatService.updateSeat(idx, request)
        );
    }

    // 좌석 삭제
    @DeleteMapping("/api/catalog/seats/{idx}")
    public ResponseEntity<Void> deleteSeat(
            @PathVariable Long idx
    ) {

        screenSeatService.deleteSeat(idx);

        return ResponseEntity.noContent().build();
    }

}

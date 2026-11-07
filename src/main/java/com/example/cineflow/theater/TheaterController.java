package com.example.cineflow.theater;

import com.example.cineflow.theater.model.TheaterDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/catalog/theaters")
public class TheaterController {

    public final TheaterService theaterService;

    // 영화관 등록
    @PostMapping
    public ResponseEntity<TheaterDto.TheaterResponse> createTheater(
            @Valid @RequestBody TheaterDto.TheaterRequest request
    ) {
        TheaterDto.TheaterResponse response = theaterService.createTheater(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // 영화관 전체 조회
    @GetMapping
    public ResponseEntity<List<TheaterDto.TheaterResponse>> getTheaters() {

        return ResponseEntity.ok(
                theaterService.getTheaters()
        );
    }

    // 영화관 상세 조회
    @GetMapping("/{idx}")
    public ResponseEntity<TheaterDto.TheaterResponse> getTheater(
            @PathVariable Long idx
    ) {

        return ResponseEntity.ok(
                theaterService.getTheater(idx)
        );
    }

    // 영화관 수정
    @PutMapping("/{idx}")
    public ResponseEntity<TheaterDto.TheaterResponse> updateTheater(
            @PathVariable Long idx,
            @Valid @RequestBody TheaterDto.TheaterRequest request
    ) {

        return ResponseEntity.ok(
                theaterService.updateTheater(idx, request)
        );
    }

    // 영화관 삭제
    @DeleteMapping("/{idx}")
    public ResponseEntity<Void> deleteTheater(
            @PathVariable Long idx
    ) {

        theaterService.deleteTheater(idx);

        return ResponseEntity.noContent().build();
    }


}

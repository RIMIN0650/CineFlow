package com.example.cineflow.queue;

import com.example.cineflow.queue.model.QueueDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    // 대기열 등록
    @PostMapping("/{screeningIdx}/enter")
    public ResponseEntity<QueueDto.QueueEnterResponse> enterQueue(
            @PathVariable Long screeningIdx,
            Authentication authentication
    ) {
        QueueDto.QueueEnterResponse response = queueService.enterQueue(
                screeningIdx,
                authentication.getName()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 대기 순번 조회
    @GetMapping("/{screeningIdx}/status")
    public ResponseEntity<QueueDto.QueueStatusResponse> getQueueStatus(
            @PathVariable Long screeningIdx,
            Authentication authentication
    ) {
        QueueDto.QueueStatusResponse response = queueService.getQueueStatus(
                screeningIdx,
                authentication.getName()
        );

        return ResponseEntity.ok(response);
    }

    // 입장 API
    @PostMapping("/{screeningIdx}/admit")
    public ResponseEntity<QueueDto.QueueAdmissionResponse> admitQueue(
            @PathVariable Long screeningIdx,
            Authentication authentication
    ) {
        QueueDto.QueueAdmissionResponse response = queueService.admitQueue(
                screeningIdx,
                authentication.getName()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{screeningIdx}/validate")
    public ResponseEntity<QueueDto.QueueTokenValidationResponse> validateToken(
            @PathVariable Long screeningIdx,
            @RequestBody QueueDto.QueueTokenValidationRequest request,
            Authentication authentication
    ) {
        QueueDto.QueueTokenValidationResponse response =
                queueService.validateToken(
                screeningIdx,
                        authentication.getName(),
                        request.token()
                );
        return ResponseEntity.ok(response);
    }

}

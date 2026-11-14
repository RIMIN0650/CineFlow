package com.example.cineflow.queue.model;

public class QueueDto {

    // 대기열 등록 결과를 반환하는 DTO
    public record QueueEnterResponse(
            Long screeningIdx,
            Long userIdx,
            Long waitingPosition,
            Long totalWaiting
    ) {}

    // 현재 대기 상태를 조회하는 DTO
    public record QueueStatusResponse(
            Long screeningIdx,
            Long userIdx,
            Long waitingPosition,
            Long totalWaiting,
            String status
    ) {}

    // 입장 토큰 발급 응답
    public record QueueAdmissionResponse(
            Long screenIdx,
            Long userIdx,
            String token,
            String status,
            Long expiresIn
    ) {}

    // 토큰 검증 요청
    public record QueueTokenValidationRequest(
            String token
    ) {}

    // 토큰 검증 응답
    public record QueueTokenValidationResponse(
            Long screeningIdx,
            Long userIdx,
            boolean valid,
            String status
    ) {}

}

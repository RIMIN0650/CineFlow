package com.example.cineflow.booking.model;

public enum BookingStatus {
    PENDING, // 예매 생성, 결제 대기
    CONFIRMED, // 결제 완료
    CANCELLED // 예매 취소
}

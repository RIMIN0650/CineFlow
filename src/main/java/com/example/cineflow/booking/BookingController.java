package com.example.cineflow.booking;

import com.example.cineflow.booking.model.BookingDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    // 예매 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto.BookingResponse createBooking(
            Authentication authentication,
            @Valid @RequestBody BookingDto.BookingRequest request
    ) {
        return bookingService.createBooking(
                authentication.getName(),
                request
        );
    }

    // 내 예매 목록
    @GetMapping("/me")
    public List<BookingDto.BookingResponse> getMyBookings(
            Authentication authentication
    ) {
        return bookingService.getMyBookings(
                authentication.getName()
        );
    }

    // 내 예매 상세
    @GetMapping("/{idx}")
    public BookingDto.BookingResponse getMyBooking(
            Authentication authentication,
            @PathVariable Long idx
    ) {
        return bookingService.getMyBooking(
                authentication.getName(),
                idx
        );
    }

    // 예매 취소
    @PatchMapping("/{idx}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelBooking(
            Authentication authentication,
            @PathVariable Long idx
    ) {
        bookingService.cancelBooking(
                authentication.getName(),
                idx
        );
    }
}
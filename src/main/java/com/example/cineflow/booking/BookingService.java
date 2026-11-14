package com.example.cineflow.booking;

import com.example.cineflow.booking.model.Booking;
import com.example.cineflow.booking.model.BookingDto;
import com.example.cineflow.booking.model.BookingSeat;
import com.example.cineflow.booking.model.BookingStatus;
import com.example.cineflow.screen.ScreenSeatRepository;
import com.example.cineflow.screen.model.ScreenSeat;
import com.example.cineflow.screening.ScreeningRepository;
import com.example.cineflow.screening.model.Screening;
import com.example.cineflow.user.UserRepository;
import com.example.cineflow.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final ScreenSeatRepository screenSeatRepository;
    private final ScreeningRepository screeningRepository;
    private final UserRepository userRepository;

    private static final List<BookingStatus> ACTIVE_STATUSES =
            List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    // 예매 생성
    @Transactional
    public BookingDto.BookingResponse createBooking(
            String email,
            BookingDto.BookingRequest request
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        Screening screening = screeningRepository.findById(
                request.screeningIdx()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "상영 일정을 찾을 수 없습니다."
        ));

        if (!screening.getStartTime().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 시작했거나 종료된 상영 일정입니다."
            );
        }

        // 요청에 중복 좌석 ID가 포함됐는지 확인
        Set<Long> requestedSeatIds =
                new HashSet<>(request.screenSeatIdxs());

        if (requestedSeatIds.size() != request.screenSeatIdxs().size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "중복된 좌석이 포함되어 있습니다."
            );
        }

        // 좌석 ID 오름차순으로 잠금
        List<ScreenSeat> seats =
                screenSeatRepository.findAllByIdxInWithLock(
                        requestedSeatIds
                );

        if (seats.size() != requestedSeatIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "존재하지 않는 좌석이 포함되어 있습니다."
            );
        }

        Long screeningScreenIdx =
                screening.getScreen().getIdx();

        // 해당 상영 일정의 상영관에 속한 좌석인지 확인
        for (ScreenSeat seat : seats) {

            if (!seat.getScreen().getIdx().equals(screeningScreenIdx)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "해당 상영 일정의 상영관에 속하지 않는 좌석입니다."
                );
            }

            boolean alreadyBooked =
                    bookingSeatRepository.existsActiveBooking(
                            screening.getIdx(),
                            seat.getIdx(),
                            ACTIVE_STATUSES
                    );

            if (alreadyBooked) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "이미 예매된 좌석이 포함되어 있습니다."
                );
            }
        }

        long totalPrice = Math.multiplyExact(
                (long) screening.getPrice(),
                seats.size()
        );

        Booking booking = new Booking(
                user,
                screening,
                totalPrice
        );

        // 예매에 선택한 좌석 연결
        for (ScreenSeat seat : seats) {

            BookingSeat bookingSeat = new BookingSeat(
                    booking,
                    screening,
                    seat,
                    screening.getPrice()
            );

            booking.addBookingSeat(bookingSeat);
        }

        Booking saved = bookingRepository.save(booking);

        return BookingDto.BookingResponse.from(saved);
    }

    // 내 예매 목록
    public List<BookingDto.BookingResponse> getMyBookings(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));


        List<Booking> bookings = bookingRepository
                .findByUser_IdxOrderByCreatedAtDesc(user.getIdx());

        List<BookingDto.BookingResponse> responses = new ArrayList<>();
        for (Booking booking : bookings) {
            responses.add(BookingDto.BookingResponse.from(booking));
        }

        return responses;

    }

    // 내 예매 상세 조회
    public BookingDto.BookingResponse getMyBooking(
            String email,
            Long bookingIdx
    ) {

        User user = findUser(email);
        Booking booking = findBooking(bookingIdx);

        validateOwner(user, booking);

        return BookingDto.BookingResponse.from(booking);
    }

    // 예매 취소
    @Transactional
    public void cancelBooking(
            String email,
            Long bookingIdx
    ) {

        User user = findUser(email);
        Booking booking = findBooking(bookingIdx);

        validateOwner(user, booking);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "결제 대기 상태의 예매만 취소할 수 있습니다."
            );
        }

        booking.cancel();
    }

    private User findUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));
    }

    private Booking findBooking(Long idx) {

        return bookingRepository.findWithDetailsByIdx(idx)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "예매 정보를 찾을 수 없습니다."
                ));
    }

    private void validateOwner(User user, Booking booking) {

        if (!booking.getUser().getIdx().equals(user.getIdx())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "본인의 예매 정보만 확인할 수 있습니다."
            );
        }
    }
}
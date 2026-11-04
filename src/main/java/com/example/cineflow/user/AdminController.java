package com.example.cineflow.user;

import com.example.cineflow.user.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signupAdmin(
            @RequestBody UserDto.AdminSignupReq dto
    ) {
        userService.signupAdmin(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("관리자 회원가입이 완료되었습니다");

    }
}

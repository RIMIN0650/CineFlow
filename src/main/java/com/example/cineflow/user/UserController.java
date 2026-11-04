package com.example.cineflow.user;

import com.example.cineflow.security.user.CustomUserDetails;
import com.example.cineflow.user.model.User;
import com.example.cineflow.user.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @RequestBody UserDto.SignupReq dto) {
        userService.signup(dto);

        return ResponseEntity.ok("회원가입 성공");
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto.UserInfoRes> getMyInfo(
            // Spring Security의 SecurityContextHolder에 저장된 인증 정보를 가져오는 기능
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();

        UserDto.UserInfoRes response = new UserDto.UserInfoRes(
                user.getIdx(),
                user.getEmail(),
                user.getName(),
                user.getRole().name()
        );

        return ResponseEntity.ok(response);
    }

}

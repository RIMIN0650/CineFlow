package com.example.cineflow.user;

import com.example.cineflow.security.jwt.JwtUtil;
import com.example.cineflow.security.user.CustomUserDetails;
import com.example.cineflow.user.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<UserDto.LoginRes> login(
            @RequestBody UserDto.LoginReq dto
    ) {
        // 1. 사용자 인증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.email(),
                        dto.password()
                )
        );

        // 2. 인증된 사용자 정보 가져오기
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();


        // 3. JWT 발급
        String token = jwtUtil.generateToken(userDetails);

        // 4. JWT 반환
        return ResponseEntity.ok(
                new UserDto.LoginRes(token, "Bearer")
        );

    }



}

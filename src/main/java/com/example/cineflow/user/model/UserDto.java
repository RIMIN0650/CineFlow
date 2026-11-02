package com.example.cineflow.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDto {

    @Getter
    @NoArgsConstructor
    public static class SignupReq {
        private String email;
        private String password;
        private String name;
    }

    public record LoginReq(
            String email,
            String password
    ) {}

    public record LoginRes(
            String accessToken,
            String tokenType
    ) {}
}

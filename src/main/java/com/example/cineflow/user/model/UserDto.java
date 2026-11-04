package com.example.cineflow.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class UserDto {


    public record SignupReq (

            @NotBlank
            @Email
            String email,

            @NotBlank
            @Size(min = 8)
            String password,

            @NotBlank
            String name
    ) {}

    public record LoginReq(
            String email,
            String password
    ) {}

    public record LoginRes(
            String accessToken,
            String tokenType
    ) {}

    public record UserInfoRes (
            Long idx,
            String email,
            String name,
            String role
    ) {}

    public record AdminSignupReq(
            @NotBlank
            @Email
            String email,

            @NotBlank
            @Size(min = 8)
            String password,

            @NotBlank
            String name
    ) {}

}

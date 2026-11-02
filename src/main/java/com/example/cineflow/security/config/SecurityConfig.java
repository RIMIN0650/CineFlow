package com.example.cineflow.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .formLogin(form -> form.disable()) // 기본 로그인 페이지 비활성화
                .httpBasic(basic -> basic.disable()) // HTTP Basic 인증 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS // 서버에서 로그인 세션을 관리하지 않음 (JWT를 이용한 인증을 구현할 것이기 때문)
                    )
                )
                .authorizeHttpRequests(auth -> auth

                        // 로그인 및 회원강비은 누구나 접근 가능
                        .requestMatchers("/api/users/signup",
                                "/api/users/login").permitAll()


                        // 관리자만 접근 가능
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // 나머지 요청은 인증 필요
                        .anyRequest().authenticated()

                );

            return http.build();


    }

    @Bean
    public PasswordEncoder passowrdEncoder() {
        return new BCryptPasswordEncoder();
    }


}
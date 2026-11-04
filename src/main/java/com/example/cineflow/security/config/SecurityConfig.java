package com.example.cineflow.security.config;

import com.example.cineflow.security.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

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

                )
                
                // Spring Security의 기본 UsernamePasswordAuthenticationFilter 보다 
                // 먼저 JWT 필터를 실행하겠다
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );




            return http.build();


    }

    @Bean
    public PasswordEncoder passowrdEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
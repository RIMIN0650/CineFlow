package com.example.cineflow.security.user;

import com.example.cineflow.user.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    // Spring Security는 자체적으로 사용자 정보를 관리하는 userDetails 인터페이스를 제공한다.
    // 우리가 만든 user 엔티티를 그대로 사용하는 게 아니라, Spring Security가 이해할 수 있는 형태로 변환하는 것.

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // DB에서는 USER, ADMIN으로 저장되지만
        // Spring Security에서는 ROLE_USER, ROLE_ADMIN 형태로 권한을 전달한다.
        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().name()
                )
        );

    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }


    @Override
    public String getUsername() {
        return user.getEmail();
    }



}

package com.example.cineflow.user.model;

import com.example.cineflow.common.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private User(String email, String password, String name, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    // 일반 사용자 생성
    public static User createUser(String email, String password, String name) {
        return new User (email, password, name, Role.USER);
    }

    // 관리자 생성
    public static User createAdmin(String email, String password, String name) {
        return new User (email, password, name, Role.ADMIN);
    }

}

package com.example.cineflow.theater.model;

import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Theater {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Theater(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.updatedAt = LocalDateTime.now();
    }


}

package com.example.cineflow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RedisConnectionTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void redisConnectionTest() {
        redisTemplate.opsForValue().set("cineflow:test", "connected");

        String result = redisTemplate.opsForValue()
                .get("cineflow:test");

        assertThat(result).isEqualTo("connected");
    }

}

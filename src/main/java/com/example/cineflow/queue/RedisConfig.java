package com.example.cineflow.queue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class RedisConfig {

    @Bean
    public DefaultRedisScript<Long> queueJoinScript() {

        String script = """
                local waitingKey = KEYS[1]
                local sequenceKey = KEYS[2]
                local userId = ARGV[1]
                
                -- 이미 대기열에 등록된 사용자인지 확인
                local existing = redis.call('ZSCORE', waitingKey, userId)
                
                if existing then
                    return 0
                end
                
                -- 대기 순번 생성
                local sequence = redis.call('INCR', sequenceKey)
                
                -- 대기열 등록
                redis.call('ZADD', waitingKey, sequence, userId)
                
                return 1
                
                """;

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();

        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);

        return redisScript;

        /*
        KEYS와 ARGV는 Lua Script에 전달하는 값.

        KEYS[1] : 대기열 Sorted Set
        KEYS[2] : 순번 증가용 Redis Key
        ARGV[1] : 사용자 ID

        이미 등록된 사용자라면 0, 새로 등록했다면 1을 반환.
         */

    }

}

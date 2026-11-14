package com.example.cineflow.queue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class RedisConfig {

    // 대기열 등록 Script
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


    // 입장 처리 Script
    @Bean
    public DefaultRedisScript<Long> queueAdmissionScript() {

        String script = """
                local waitingKey = KEYS[1]
                local activeKey = KEYS[2]
                local tokenKey = KEYS[3]
                
                local userId = ARGV[1]
                local token = ARGV[2]
                local now = tonumber(ARGV[3])
                local expiresAt = tonumber(ARGV[4])
                local maxActive = tonumber(ARGV[5])
                local ttl = tonumber(ARGV[6])
                
                -- 만료된 입장 사용자 제거
                redis.call('ZREMRANGEBYSCORE', activeKey, '-inf', now)
                
                -- 이미 입장한 사용자인지 확인
                local existing = redis.call('ZSCORE', activeKey, userId)
                
                if existing then
                    return 2
                end
                
                -- 현재 입장 인원 확인
                local activeCount = redis.call('ZCARD', activeKey)
                
                if activeCount >= maxActive then
                    return 0
                end
                
                -- 대기열에서 가장 앞에 있는 사용자 확인
                local firstUser = redis.call('ZRANGE', waitingKey, 0, 0)
                
                if not firstUser or firstUser[1] ~= userId then
                    return 3
                end
                
                -- 대기열에서 제거
                redis.call('ZREM', waitingKey, userId)
                
                -- 입장 사용자 등록 (Score = 만료 시각)
                redis.call('ZADD', activeKey, expiresAt, userId)
                
                -- 입장 토큰 저장
                redis.call('SET', tokenKey, token, 'EX', ttl)
                
                return 1
                
                """;

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);

        return redisScript;

        /*
            1. 입장 성공 및 토큰 발급
            0. 최대 동시 입장 인원 초과
            2. 이미 입장한 사용자
            3. 아직 대기 순서가 되지 않음
         */

    }


}

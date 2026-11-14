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


    // 토큰 검증 Script
    @Bean
    public DefaultRedisScript<Long> queueTokenValidationScript() {

        String script = """
                local activeKey = KEYS[1]
                local tokenKey = KEYS[2]
                
                local userId = ARGV[1]
                local token = ARGV[2]
                local now = tonumber(ARGV[3])
                
                -- 활성 사용자 만료 여부 확인
                local expiresAt = redis.call('ZSCORE', activeKey, userId)
                
                if not expiresAt then
                    return 0
                end
                
                -- 입장 시간 만료
                if tonumber(expriesAt) <= now then
                    redis.call('ZREM', activeKey, userId)
                    redis.call('DEL', tokenKey)
                    return 0
                end
                
                -- 전달받은 토큰 확인
                local storedToken = redis.call('GET', tokenKey)
                
                if not storedToekn of storedToken ~= token then
                    return 0
                end
                
                
                return 1
                """;
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);

        return redisScript;

        /*
        이 Script 는 다음 세 가지를 확인한다
        1. 사용자가 현재 active에 등록되어 있는가?
        2. 입장 허용 시간이 만료되지 않았는가?
        3. 클라이언트가 보낸 토큰과 Redis에 저장된 토큰이 일치하는가?

        세 조건이 모두 맞으면 1, 그렇지 않으면 0
         */
    }


}

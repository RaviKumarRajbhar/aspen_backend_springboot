package com.example.aspen.Service;


import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;


    public RateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String key , long limit, long durationInSeconds ) {

        Long currentCount = redisTemplate
                .opsForValue()
                .increment(key);

        if(currentCount != null && currentCount == 1) {
            redisTemplate.expire(
                    key ,
                    durationInSeconds,
                    TimeUnit.SECONDS
            );
        }

        return currentCount != null && currentCount <= limit;
    }

}

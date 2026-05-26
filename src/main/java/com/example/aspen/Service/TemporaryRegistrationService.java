package com.example.aspen.Service;


import com.example.aspen.Dto.TemporaryRegistration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TemporaryRegistrationService {

    private final RedisTemplate<String , Object> redisTemplate;

    private static final String PREFIX = "register:";

    public TemporaryRegistrationService(
            @Qualifier("otpTemplate")
            RedisTemplate<String , Object> redisTemplate
    ){
        this.redisTemplate = redisTemplate;
    }

    public  void save(
            String email,
            TemporaryRegistration data
    ){
        redisTemplate.opsForValue().set(
                PREFIX + email,
                data,
                Duration.ofMinutes(5)
        );
    }

    public TemporaryRegistration get(String email) {
        return  (TemporaryRegistration) redisTemplate.opsForValue().get(PREFIX + email);
    }

    public void delete(String email) {
        redisTemplate.delete(PREFIX +email);
    }


}

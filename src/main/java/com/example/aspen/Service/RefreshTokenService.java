package com.example.aspen.Service;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {

    private final RedisTemplate<String,String > redisTemplate ;


    public RefreshTokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public void saveRefreshToken(String username , String refresh){
        redisTemplate.opsForValue().set(
                "refresh:" + username,
                refresh,
                7,
                TimeUnit.DAYS
        );
    }

    public boolean acquireRefreshLock(String userId) {

        Boolean success = redisTemplate.opsForValue().setIfAbsent(
                "lock:refresh" + userId,
                "locked",
                10,
                TimeUnit.SECONDS
        );

        return Boolean.TRUE.equals(success);

    }

    public void releaseRefreshLock(String userId){
        redisTemplate.delete("lock:refresh" + userId);
    }

    public String getRefreshToken(String username) {
        return redisTemplate.opsForValue().get("refresh:" + username);
    }

    public void deleteToken(String username){
        redisTemplate.delete(username);
    }




}

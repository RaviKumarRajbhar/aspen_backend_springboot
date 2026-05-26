package com.example.aspen.Service;


import com.example.aspen.CustomException.InvalidCredentialsException;
import com.example.aspen.CustomException.ResourceNotFoundException;
import com.example.aspen.Entities.User;
import com.example.aspen.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PasswordResetService {


    private final UserRepository userRepository;

    private final RedisTemplate<String , String> redisTemplate;

    private final MailService mailService;

    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UserRepository userRepository, @Qualifier("redisTemplate") RedisTemplate redisTemplate, MailService mailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }

    public void  forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("User Not Found"));

        String token = UUID.randomUUID().toString();

        String redisKey = "reset:" + token;

        redisTemplate.opsForValue().set(
                redisKey,
                user.getId().toString(),
                10,
                TimeUnit.MINUTES
        );

        mailService.sendResetEmail(
                user.getEmail(),
                token );

    }

    public void resetPassword (String token , String newPassword) {

        String redisKey = "reset:" + token;

        String userId = redisTemplate.opsForValue().get(redisKey);

        if (userId == null) {
            throw new InvalidCredentialsException("Invalid or Expired Token");
        }

        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(()-> new ResourceNotFoundException("User Not Found"));

        String encodedPassword = passwordEncoder.encode(newPassword);

        user.setPassword(encodedPassword);

        userRepository.save(user);

        redisTemplate.delete(redisKey);
    }
}

package com.example.aspen.Service;


import com.example.aspen.CustomException.InvalidCredentialsException;
import com.example.aspen.CustomException.ResourceNotFoundException;
import com.example.aspen.CustomException.UserAlreadyExistsException;
import com.example.aspen.CustomException.WrongAuthProviderException;
import com.example.aspen.Dto.LoginRequest;
import com.example.aspen.Dto.LoginResponse;
import com.example.aspen.Dto.RegisterRequest;
import com.example.aspen.Entities.User;
import com.example.aspen.Repository.UserRepository;
import com.example.aspen.Security.AuthProvider;
import com.example.aspen.Security.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GoogleTokenVerifierService verifierService;


    public AuthService(JwtUtil jwtUtil, RefreshTokenService refreshTokenService, UserRepository userRepository, PasswordEncoder passwordEncoder, GoogleTokenVerifierService verifierService) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verifierService = verifierService;
    }


    public LoginResponse loginWithGoogle(String idToken) throws Exception {


        GoogleIdToken.Payload payload =
                verifierService.verify(
                        idToken
                );

        String email =
                payload.getEmail();

        String name =
                (String) payload.get("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(name);
                    newUser.setAuthProvider(AuthProvider.GOOGLE); // google login


                    return userRepository.save(newUser);
                }

        );

        if(user.getAuthProvider() != AuthProvider.GOOGLE){
            throw new WrongAuthProviderException("Please Login through password");
        }

        String userId = user.getId().toString();
        String accessToken = jwtUtil.generateAccessToken(userId);
        String refreshToken = jwtUtil.generateRefreshToken(userId);

        refreshTokenService.saveRefreshToken(userId , refreshToken);


        return new LoginResponse(accessToken , refreshToken);



    }

    public LoginResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email not available");
        }

        if(request.getPassword() == null || request.getPassword().isBlank()){
            throw new InvalidCredentialsException("Password is Required");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        String username = request.getUsername();

        if (username == null || username.isBlank()) {
            username = "user_" + UUID.randomUUID().toString().substring(0, 8);
        }

        User user = new User(
                username,
                hashedPassword,
                request.getEmail()
        );

        user.setAuthProvider(AuthProvider.LOCAL); // local user

        user.validateAuthData();

        User savedUser = userRepository.save(user);

        String userId = savedUser.getId().toString();

        String accessToken = jwtUtil.generateAccessToken(userId);
        String refreshToken = jwtUtil.generateRefreshToken(userId);

        refreshTokenService.saveRefreshToken(userId, refreshToken);

        return new LoginResponse(accessToken , refreshToken);


    }



    public LoginResponse login(LoginRequest request){

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not Found"));

        if(user.getAuthProvider() != AuthProvider.LOCAL){
            throw new RuntimeException("Please Login through Google"); // message should be generalized in case of multiple providers
        }

        if(request.getPassword() == null || request.getPassword().isBlank()){
            throw new InvalidCredentialsException("Password is Required");
        }

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Incorrect Password");
        }

        String userId = user.getId().toString();

        String accessToken = jwtUtil.generateAccessToken(userId);
        String refreshToken = jwtUtil.generateRefreshToken(userId);

        refreshTokenService.saveRefreshToken( userId , refreshToken);

        return new LoginResponse(accessToken , refreshToken);

    }


    public LoginResponse refresh(String refreshToken) {

        String userId = jwtUtil.extractUserId(refreshToken);

        boolean lockAcquired = refreshTokenService.acquireRefreshLock(userId);

        if (!lockAcquired) {
            throw new RuntimeException("Refresh already in Progress");
        }

        try {


            // Redis check
            String storedToken = refreshTokenService.getRefreshToken(userId);


            if (storedToken == null || !storedToken.equals(refreshToken)) {
                throw new RuntimeException("Invalid refresh Token");
            }


            // JWT validation
            if (!jwtUtil.validateToken(refreshToken)) {
                throw new RuntimeException("Invalid refresh token");
            }

            String newAccessToken = jwtUtil.generateAccessToken(userId);
            String newRefreshToken = jwtUtil.generateRefreshToken(userId);

            refreshTokenService.saveRefreshToken(userId, newRefreshToken);
            return new LoginResponse(newAccessToken, newRefreshToken);


        } finally {
            refreshTokenService.releaseRefreshLock(userId);
        }
    }


    public void logout(String refreshToken) {

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid Refresh Token");
        }

        String userId = jwtUtil.extractUserId(refreshToken);

        refreshTokenService.deleteToken(userId);
    }


}

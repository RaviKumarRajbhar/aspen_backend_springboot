package com.example.aspen.Controller.Auth;


import com.example.aspen.Dto.*;
import com.example.aspen.Service.AuthService;
import com.example.aspen.Service.GoogleTokenVerifierService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostConstruct
    public void init() {
        System.out.println("AuthController loaded");
    }

    private final AuthService authService;

    public AuthController(
            AuthService authService) {

        this.authService = authService;
    }

    @PostMapping("/register/initiate")
    public String initiateRegister(@RequestBody RegisterRequest request) {
        return authService.initiateRegister(request);
    }

    @PostMapping("/register/verify")
    public LoginResponse verifyRegister(@RequestBody VerifyOtpRequest request) {
        return  authService.verifyRegister(request);
    }



    @PostMapping("/google")
    public LoginResponse loginWithGoogle(
            @RequestBody
            GoogleLoginRequest request
    ) throws Exception {

        return authService.loginWithGoogle(request.getIdToken()) ;
    }

    @PostMapping("/login")
    public LoginResponse login (@RequestBody  LoginRequest request){
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@RequestBody RefreshRequest request ){
        return authService.refresh(request.getRefreshToken());
    }

    @PostMapping("/logout")
    public String logout(@RequestBody String refreshToken){
        authService.logout(refreshToken);
        return "Logged Out Successfully";
    }
























}

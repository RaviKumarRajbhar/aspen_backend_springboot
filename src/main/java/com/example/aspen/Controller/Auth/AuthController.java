package com.example.aspen.Controller.Auth;


import com.example.aspen.Dto.*;
import com.example.aspen.Service.AuthService;
import com.example.aspen.Service.GoogleTokenVerifierService;
import com.example.aspen.Service.PasswordResetService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostConstruct
    public void init() {
        System.out.println("AuthController loaded");
    }

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(
            AuthService authService, PasswordResetService passwordResetService) {

        this.authService = authService;
        this.passwordResetService = passwordResetService;
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

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request){
        passwordResetService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Reset link has been sent to your email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken() , request.getNewPassword());

        return  ResponseEntity.ok("Password reset successful");
    }

    @PostMapping("/logout")
    public String logout(@RequestBody String refreshToken){
        authService.logout(refreshToken);
        return "Logged Out Successfully";
    }

}

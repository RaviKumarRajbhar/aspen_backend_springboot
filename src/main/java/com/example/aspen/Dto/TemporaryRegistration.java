package com.example.aspen.Dto;

public class TemporaryRegistration {

    private String email;
    private String username;
    private String hashedPassword;
    private String otp;

    public TemporaryRegistration(){}

    public TemporaryRegistration(
            String email,
            String username,
            String hashedPassword,
            String otp
    ) {
        this.email = email;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.otp = otp;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getOtp() {
        return otp;
    }

}

package com.example.aspen.Dto;

public class LoginResponse {

    public LoginResponse(String accessToken , String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;

    }

    private final String accessToken;
    private final String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}

package com.example.aspen.Dto;

import jakarta.validation.constraints.NotBlank;

public class RefreshRequest {

    @NotBlank
    private String refreshToken;

    public RefreshRequest(){}

    public String getRefreshToken() {
        return refreshToken;
    }
}

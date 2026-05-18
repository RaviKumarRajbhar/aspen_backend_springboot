package com.example.aspen.Dto;

import java.util.UUID;

public class UserSummaryDto {

    UserSummaryDto(){}

    public UserSummaryDto(UUID id, String username, String bio){
        this.id = id;
        this.bio = bio;
        this.username = username;
    }

    private UUID id;
    private String username;
    private String bio;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public UUID getId() {
        return id;
    }
}

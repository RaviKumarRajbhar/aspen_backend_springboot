package com.example.aspen.Dto;

import java.util.UUID;

public class CommentResponse {

    private UUID id;
    private String content;
    private String username;

    public CommentResponse(UUID id, String content, String username) {
        this.id = id;
        this.content = content;
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }

    public UUID getId() {
        return id;
    }


}

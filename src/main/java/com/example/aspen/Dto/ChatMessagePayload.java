package com.example.aspen.Dto;

import java.util.UUID;

public class ChatMessagePayload {

    private UUID receiverId;

    private String content;

    public UUID getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

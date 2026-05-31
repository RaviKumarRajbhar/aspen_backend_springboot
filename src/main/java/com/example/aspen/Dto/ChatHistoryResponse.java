package com.example.aspen.Dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ChatHistoryResponse {

    private List<ChatMessageResponse> messages;

    private LocalDateTime nextCursorCreatedAt;

    private UUID nextCursorId;

    private boolean hasNext;

    public List<ChatMessageResponse> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessageResponse> messages) {
        this.messages = messages;
    }

    public LocalDateTime getNextCursorCreatedAt() {
        return nextCursorCreatedAt;
    }

    public void setNextCursorCreatedAt(LocalDateTime nextCursorCreatedAt) {
        this.nextCursorCreatedAt = nextCursorCreatedAt;
    }

    public UUID getNextCursorId() {
        return nextCursorId;
    }

    public void setNextCursorId(UUID nextCursorId) {
        this.nextCursorId = nextCursorId;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
}

package com.example.aspen.Dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class FeedResponse {

    private List<PostResponse> posts;
    private LocalDateTime nextCursorCreatedAt;
    private UUID nextCursorId;
    private boolean hasNext;

    public List<PostResponse> getPosts() {
        return posts;
    }

    public void setPosts(List<PostResponse> posts) {
        this.posts = posts;
    }

    public LocalDateTime getNextCursorCreatedAt() {
        return nextCursorCreatedAt;
    }

    public void setNextCursorCreatedAt(LocalDateTime nextCursorCreatedAt) {
        this.nextCursorCreatedAt = nextCursorCreatedAt;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public UUID getNextCursorId() {
        return nextCursorId;
    }

    public void setNextCursorId(UUID nextCursorId) {
        this.nextCursorId = nextCursorId;
    }
}

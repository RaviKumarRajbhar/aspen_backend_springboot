package com.example.aspen.Security.RateLimit;

public class RateLimitRule {

    private final long limit;
    private final long durationInSeconds;

    public RateLimitRule(long limit, long durationInSeconds) {
        this.limit = limit;
        this.durationInSeconds = durationInSeconds;
    }

    public long getLimit() {
        return limit;
    }

    public long getDurationInSeconds() {
        return durationInSeconds;
    }
}

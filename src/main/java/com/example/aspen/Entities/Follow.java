package com.example.aspen.Entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "follows", uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id" , "following_id"}))
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "follower_id" , nullable = false)
    private User follower;

    @ManyToOne
    @JoinColumn(name = "following_id" , nullable = false)
    private User following;

    private final LocalDateTime createdAt = LocalDateTime.now();

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public void setFollowing(User following) {
        this.following = following;
    }

    public User getFollower() {
        return follower;
    }

    public User getFollowing() {
        return following;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}

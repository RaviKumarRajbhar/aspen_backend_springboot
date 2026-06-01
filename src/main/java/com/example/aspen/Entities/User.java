package com.example.aspen.Entities;


import com.example.aspen.Security.AuthProvider;
import jakarta.persistence.*;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ConditionalOnIssuerLocationJwtDecoder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false , unique = true)
    private String username;

    @Column(nullable = false , unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private String bio = "";

    private boolean online;

    private LocalDateTime lastSeenAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY , cascade = CascadeType.REMOVE)
    private List<Post> posts;

    @Column(nullable = false)
    private int followersCount = 0;

    @Column(nullable = false)
    private int followingCount = 0;

    @Column(nullable = false)
    private int postCount = 0;

    @Column
    private String userProfileUrl;

    @CreatedDate
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public User(){}

    public User(String username,
                String password,
                String email){

        this.username = username;
        this.password = password;
        this.email = email;

    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }



    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getBio() {
        return bio;
    }

    public String getUsername() {
        return username;
    }

    public String getUserProfileUrl() {
        return userProfileUrl;
    }

    public void setUserProfileUrl(String userProfileUrl) {
        this.userProfileUrl = userProfileUrl;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public void setFollowingCount(int followingsCount) {
        this.followingCount = followingsCount;
    }

    public UUID getId() {
        return id;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void validateAuthData() {

        if (authProvider == AuthProvider.LOCAL &&
                (password == null || password.isBlank())) {

            throw new IllegalArgumentException(
                    "Password required for LOCAL login"
            );
        }

    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public LocalDateTime getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(LocalDateTime lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }
}
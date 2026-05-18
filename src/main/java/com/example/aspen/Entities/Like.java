package com.example.aspen.Entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "likes" , uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","post_id"}))
public class Like {

    protected Like(){}

    public Like(User user , Post post){
        this.user = user;
        this.post = post;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id" , nullable = false)
    private Post post;

    public User getUser() {
        return user;
    }

    public UUID getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }



}

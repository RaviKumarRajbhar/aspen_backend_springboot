package com.example.aspen.Entities;


import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "comments")
public class Comment {

    protected Comment(){}

    public Comment(User user , Post post , String content ){
        this.user = user;
        this.post = post;
        this.content = content;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false , columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id" , nullable = false)
    private Post post;


    public Post getPost() {
        return post;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }


    public String getContent() {
        return content;
    }


}

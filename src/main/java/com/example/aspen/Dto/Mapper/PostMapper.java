package com.example.aspen.Dto.Mapper;


import com.example.aspen.Dto.PostResponse;
import com.example.aspen.Entities.Post;

public class PostMapper {

    public static PostResponse toResponse(Post post) {
        PostResponse res = new PostResponse();

        res.setId(post.getId());
        res.setCaption(post.getCaption());
        res.setLikeCount(post.getLikeCount());
        res.setCreatedAt(post.getCreatedAt());
        res.setCommentCount(post.getCommentCount());
        res.setLandscape(post.getLandscape());
        res.setImageUrl(post.getImageUrl());


        res.setUserId(post.getUser().getId());
        res.setUsername(post.getUser().getUsername());

        return res;
    }
}

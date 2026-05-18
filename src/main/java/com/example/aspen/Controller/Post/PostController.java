package com.example.aspen.Controller.Post;


import com.example.aspen.Dto.PostRequest;
import com.example.aspen.Dto.PostResponse;
import com.example.aspen.Entities.Post;
import com.example.aspen.Service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {


    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(consumes = {
            "multipart/form-data"
    })
    public ResponseEntity<?> createPost(
            @RequestPart("data")
            PostRequest request,

            @RequestPart("image")
            MultipartFile image,

            Authentication authentication
    ) throws Exception {

        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);

        PostResponse post = postService.createPost(userId, request , image);

        return ResponseEntity.ok(post);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyPosts(
            Authentication authentication
    ) {
        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);

        List<PostResponse> posts = postService.getAllPostsByUserId(userId);

        return ResponseEntity.ok(posts);
    }


    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(
            @PathVariable UUID postId
    ){
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable UUID postId,
            Authentication authentication
    ){
        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);

        postService.deletePost(userId,postId);
        return ResponseEntity.ok("Post Deleted Successfully");
    }



}

package com.example.aspen.Controller.Comment;


import com.example.aspen.Dto.CommentRequest;
import com.example.aspen.Service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    @PostMapping("/{postId}")
    public ResponseEntity<?> addComment(
            @PathVariable UUID postId,
            @RequestBody CommentRequest request,
            Authentication authentication
    ){
        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);

        commentService.createComment(userId,postId,request.getContent());

        return ResponseEntity.status(201).build();


    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getComments(
            @PathVariable UUID postId
    ){
        return ResponseEntity.ok(commentService.getComments(postId));

    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable UUID commentId,
            Authentication authentication
    ){
        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);

        commentService.deleteComment(userId,commentId);

        return ResponseEntity.noContent().build();

    }




}





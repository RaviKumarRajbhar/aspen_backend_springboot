package com.example.aspen.Controller.Like;

import com.example.aspen.Service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }


    @PostMapping("/{postId}")
    public ResponseEntity<?> toggleLike(
            @PathVariable UUID postId,
            Authentication authentication
    ){
        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);


        boolean response = likeService.toggleLike(userId,postId);

        if(response){
            return ResponseEntity.ok("Liked");
        } else {
            return ResponseEntity.ok("Like Removed");
        }

    }


}

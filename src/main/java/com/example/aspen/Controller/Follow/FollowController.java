package com.example.aspen.Controller.Follow;

import com.example.aspen.Dto.UserSummaryDto;
import com.example.aspen.Entities.User;
import com.example.aspen.Service.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/follow")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/{id}/follow-toggle")
    public ResponseEntity<?> toggleFollow(
            @PathVariable UUID id,
            Authentication authentication
    ){
        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);

        boolean isNowFollowing = followService.toggleFollow(userId , id);

        return  ResponseEntity.ok(Map.of(
                "following" , isNowFollowing
        ));
    }

    @GetMapping("/followers")
    public ResponseEntity<?> followersList(
            Authentication authentication
    ){
        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);

        List<UserSummaryDto> followers = followService.getFollowers(userId);

        return ResponseEntity.ok(followers);

    }

    @GetMapping("/following")
    public ResponseEntity<?> followingList (
            Authentication authentication
    ) {
         String userIdStr = (String) authentication.getPrincipal();
         UUID userId = UUID.fromString(userIdStr);

         List<UserSummaryDto> followings = followService.getFollowing(userId);

         return ResponseEntity.ok(followings);
    }

}

package com.example.aspen.Controller.Follow;

import com.example.aspen.Dto.PagedResponse;
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
    public ResponseEntity<PagedResponse<UserSummaryDto>> followersList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Authentication authentication
    ){
        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);

        PagedResponse<UserSummaryDto> followers = followService.getFollowers(userId , page , size);

        return ResponseEntity.ok(followers);

    }

    @GetMapping("/following")
    public ResponseEntity<PagedResponse<UserSummaryDto>> followingList (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Authentication authentication
    ) {
         String userIdStr = (String) authentication.getPrincipal();
         UUID userId = UUID.fromString(userIdStr);

         PagedResponse<UserSummaryDto> followings = followService.getFollowing(userId , page , size);

         return ResponseEntity.ok(followings);
    }

}

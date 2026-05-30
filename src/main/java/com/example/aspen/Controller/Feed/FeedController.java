package com.example.aspen.Controller.Feed;

import com.example.aspen.Dto.FeedResponse;
import com.example.aspen.Dto.PagedResponse;
import com.example.aspen.Dto.PostResponse;
import com.example.aspen.Service.FeedService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;


@RestController
@RequestMapping("/feed")
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }


    @GetMapping()
    public ResponseEntity<FeedResponse> getPagedFeed (
            @RequestParam(required = false) LocalDateTime cursorCreatedAt,
            @RequestParam(required = false) UUID cursorId,
            Authentication authentication
    ) {

        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);

        FeedResponse response = feedService.getFeedCursor( userId , cursorCreatedAt , cursorId);

        return ResponseEntity.ok(response);

    }
}

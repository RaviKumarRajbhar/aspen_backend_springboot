package com.example.aspen.Controller.Feed;

import com.example.aspen.Dto.PagedResponse;
import com.example.aspen.Dto.PostResponse;
import com.example.aspen.Service.FeedService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping("/feed")
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }


    @GetMapping()
    public ResponseEntity<PagedResponse<PostResponse>> getPagedFeed (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Authentication authentication
    ) {

        String userIdStr = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdStr);

        PagedResponse<PostResponse> response = feedService.getFeed( userId , page , size);

        return ResponseEntity.ok(response);

    }
}

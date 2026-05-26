package com.example.aspen.Controller.Notification;

import com.example.aspen.Dto.NotificationResponse;
import com.example.aspen.Service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(Authentication authentication) {

        String userIdStr = authentication.getPrincipal().toString();
        UUID userId = UUID.fromString(userIdStr);

        List<NotificationResponse> body = notificationService.getNotifications(userId);

        return ResponseEntity.ok(body);
    }
}

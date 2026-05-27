package com.example.aspen.Controller;

import com.example.aspen.Service.PushNotificationService;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestNotificationController {

    private final PushNotificationService
            pushNotificationService;

    public TestNotificationController(
            PushNotificationService
                    pushNotificationService
    ) {
        this.pushNotificationService =
                pushNotificationService;
    }

    @GetMapping("/test-notification")
    public String sendNotification(

            @RequestParam String token

    ) throws FirebaseMessagingException {

        pushNotificationService
                .sendTestNotification(token);

        return "Notification Sent";
    }
}
package com.example.aspen.Service;

import com.example.aspen.Entities.UserDeviceToken;
import com.example.aspen.Repository.UserDeviceTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PushNotificationService {

    private final UserDeviceTokenRepository tokenRepository;


    public PushNotificationService(UserDeviceTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Async("taskExecutor")
    public void sendNotification(UUID receiverId , String title , String body) {

        System.out.println("PUSH SERVICE CALLED");

        List<UserDeviceToken> devices = tokenRepository.findByUserIdAndIsActiveTrue(receiverId);

        for (UserDeviceToken device : devices ) {

            try {
                Message message = Message.builder()
                        .setToken(device.getFcmToken())
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .build();

                FirebaseMessaging.getInstance().send(message);

            } catch ( FirebaseMessagingException e ) {

                if (e.getMessagingErrorCode() != null && e.getMessagingErrorCode().name().equals("UNREGISTERED")) {
                    device.setActive(false);
                    tokenRepository.save(device);

                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
    }

    public void sendTestNotification(
            String token
    ) throws FirebaseMessagingException {

        Message message = Message.builder()
                          .setToken(token)
                          .setNotification(
                                Notification.builder()
                                        .setTitle("Aspen")
                                        .setBody("Notification working")
                                        .build()
                        )

                        .build();

        FirebaseMessaging
                .getInstance()
                .send(message);
    }
}

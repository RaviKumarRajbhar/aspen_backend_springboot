package com.example.aspen.Service;


import com.example.aspen.CustomException.ResourceNotFoundException;
import com.example.aspen.Dto.Mapper.NotificationMapper;
import com.example.aspen.Dto.NotificationResponse;
import com.example.aspen.Entities.Notification;
import com.example.aspen.Entities.NotificationType;
import com.example.aspen.Repository.NotificationRepository;
import com.example.aspen.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationMapper = notificationMapper;
    }

    public void createNotification(UUID receiver , UUID sender , NotificationType type , UUID referenceId) {

        Notification notification = new Notification();

        notification.setReceiverId(receiver);
        notification.setSenderId(sender);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }


    public List<NotificationResponse> getNotifications(UUID userId) {

        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        List<NotificationResponse> response = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId)
                .stream().map(notificationMapper::toResponse).toList();

        return response;
    }

    public void deleteLikeNotification(UUID senderId , UUID postId) {

        notificationRepository.deleteBySenderIdAndReferenceIdAndType(senderId , postId , NotificationType.LIKE);
    }

    public boolean hasRecentLikeNotification(
            UUID senderId,
            UUID receiverId,
            UUID referenceId,
            Duration cooldown
    ) {
        return notificationRepository.existsBySenderIdAndReceiverIdAndReferenceIdAndTypeAndCreatedAtAfter(
                senderId,
                receiverId,
                referenceId,
                NotificationType.LIKE,
                LocalDateTime.now().minus(cooldown));
    }

    public boolean hasRecentFollowNotification(
            UUID senderId,
            UUID receiverId,
            Duration cooldown
    ) {
        return notificationRepository.existsBySenderIdAndReceiverIdAndTypeAndCreatedAtAfter(
                senderId,
                receiverId,
                NotificationType.FOLLOW,
                LocalDateTime.now().minus(cooldown)
        );
    }
}

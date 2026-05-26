package com.example.aspen.Repository;

import com.example.aspen.Entities.Notification;
import com.example.aspen.Entities.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification , UUID> {


    List<Notification> findByReceiverIdOrderByCreatedAtDesc(UUID receiver);


    void deleteBySenderIdAndReferenceIdAndType(UUID senderId , UUID postId , NotificationType type);

}

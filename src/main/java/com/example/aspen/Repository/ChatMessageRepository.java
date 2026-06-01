package com.example.aspen.Repository;

import com.example.aspen.Entities.ChatMessage;
import com.example.aspen.Entities.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage , UUID> {

    @Query("""
SELECT m FROM ChatMessage m
WHERE
(
    m.sender.id = :currentUserId
    AND
    m.receiver.id = :otherUserId
)
OR
(
    m.sender.id = :otherUserId
    AND
    m.receiver.id = :currentUserId
)
ORDER BY m.createdAt ASC
""")
    List<ChatMessage> getConversation(
            @Param("currentUserId") UUID currentUserId,
            @Param("otherUserId") UUID otherUserId
    );


    @Query("""
SELECT m
FROM ChatMessage m
WHERE
(
    m.sender.id = :currentUserId
    AND
    m.receiver.id = :otherUserId
)
OR
(
    m.sender.id = :otherUserId
    AND
    m.receiver.id = :currentUserId
)
ORDER BY m.createdAt DESC, m.id DESC
""")
    List<ChatMessage> getLatestConversation(
            UUID currentUserId,
            UUID otherUserId,
            Pageable pageable
    );

    @Query("""
SELECT m 
FROM ChatMessage m 
WHERE
(
(
m.sender.id = :currentUserId
AND
m.receiver.id = :otherUserId
)
OR
(
m.sender.id = :otherUserId
AND 
m.receiver.id = :currentUserId
)
)
AND
(
:cursorCreatedAt IS NULL
OR
m.createdAt < :cursorCreatedAt
OR 
(
m.createdAt = :cursorCreatedAt
AND 
m.id < :cursorId
)
)
ORDER BY m.createdAt DESC , m.id DESC
""")
    List<ChatMessage> getConversationAfterCursor(
            UUID currentUserId,
            UUID otherUserId,
            LocalDateTime cursorCreatedAt,
            UUID cursorId,
            Pageable pageable
    );

    @Query("""
SELECT m 
FROM ChatMessage m
WHERE
m.receiver.id = :userId
AND
m.status = com.example.aspen.Entities.MessageStatus.SENT
""")
    List<ChatMessage> findPendingMessages(UUID userId);


    @Query("""
SELECT m
FROM ChatMessage m
WHERE
m.sender.id = :otherUserId
AND
m.receiver.id = :currentUserId
AND
m.status = com.example.aspen.Entities.MessageStatus.DELIVERED
""")
    List<ChatMessage> findDeliveredMessagesToMarkSeen(
            UUID currentUserId,
            UUID otherUserId
    );

    List<ChatMessage> findBySenderIdOrReceiverIdOrderByCreatedAtDesc(
            UUID senderId,
            UUID receiverId
    );

    long countBySenderIdAndReceiverIdAndStatusNot(
            UUID senderId,
            UUID receiverId,
            MessageStatus status
    );




}

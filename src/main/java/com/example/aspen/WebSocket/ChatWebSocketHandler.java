package com.example.aspen.WebSocket;

import com.example.aspen.CustomException.ResourceNotFoundException;
import com.example.aspen.Dto.ChatMessagePayload;
import com.example.aspen.Dto.ChatMessageResponse;
import com.example.aspen.Entities.ChatMessage;
import com.example.aspen.Entities.MessageStatus;
import com.example.aspen.Entities.User;
import com.example.aspen.Repository.ChatMessageRepository;
import com.example.aspen.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatSessionManager sessionManager;

    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;

    private final ChatMessageRepository chatMessageRepository;

    public ChatWebSocketHandler(ChatSessionManager sessionManager, ObjectMapper objectMapper, UserRepository userRepository, ChatMessageRepository chatMessageRepository) {
        this.sessionManager = sessionManager;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        UUID userId = (UUID) session
                .getAttributes()
                .get("userId");

        sessionManager.addSession( userId, session );


        List<ChatMessage> pendingMessages = chatMessageRepository.findPendingMessages(userId);

        for (ChatMessage message : pendingMessages) {
            message.setStatus(MessageStatus.DELIVERED);
        }
        chatMessageRepository.saveAll(pendingMessages);


        User user = userRepository.findById(userId)
                .orElseThrow();

        user.setOnline(true);
        userRepository.save(user);

    }

    @Override
    protected void handleTextMessage (WebSocketSession session , TextMessage message ) throws Exception {

        ChatMessagePayload payload = objectMapper.readValue(message.getPayload(), ChatMessagePayload.class);

        UUID senderId = (UUID) session.getAttributes()
                        .get("userId");

        User sender = userRepository.findById(senderId)
                .orElseThrow();

        User receiver = userRepository.findById(payload.getReceiverId()).orElseThrow();

        ChatMessage chatMessage = new ChatMessage();

        chatMessage.setSender(sender);
        chatMessage.setReceiver(receiver);
        chatMessage.setContent(payload.getContent());
        chatMessage.setStatus(MessageStatus.SENT);

        chatMessage = chatMessageRepository.save(chatMessage);

        ChatMessageResponse response = new ChatMessageResponse();

        response.setId(chatMessage.getId());
        response.setSenderId(sender.getId());
        response.setSenderUsername(sender.getUsername());
        response.setReceiverId(receiver.getId());
        response.setContent(chatMessage.getContent());
        response.setCreatedAt(chatMessage.getCreatedAt());
        response.setStatus(chatMessage.getStatus());


        if (sessionManager.isOnline(receiver.getId())) {

            WebSocketSession receiverSession =
                    sessionManager.getSession(receiver.getId());

            if (receiverSession != null
                    && receiverSession.isOpen()) {

                chatMessage.setStatus(
                        MessageStatus.DELIVERED
                );

                chatMessageRepository.save(
                        chatMessage
                );

                response.setStatus(
                        MessageStatus.DELIVERED
                );

                String json =
                        objectMapper.writeValueAsString(
                                response
                        );

                receiverSession.sendMessage(
                        new TextMessage(json)
                );
            }
        }

    }

    @Override public void afterConnectionClosed (WebSocketSession session , CloseStatus status ) throws Exception {

        UUID userId = (UUID) session
                .getAttributes()
                .get("userId");

        sessionManager.removeSession(userId);

        User user = userRepository.findById(userId)
                .orElseThrow();

        user.setOnline(false);
        user.setLastSeenAt(LocalDateTime.now());

        userRepository.save(user);

    }


}

package com.example.aspen.Service;

import com.example.aspen.Dto.ChatConversationResponse;
import com.example.aspen.Dto.ChatHistoryResponse;
import com.example.aspen.Dto.ChatMessageResponse;
import com.example.aspen.Entities.ChatMessage;
import com.example.aspen.Entities.MessageStatus;
import com.example.aspen.Entities.User;
import com.example.aspen.Repository.ChatMessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;


    public ChatService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getConversation (UUID currentUserId , UUID otherUserId ) {

        List<ChatMessage> messages = chatMessageRepository.getConversation(currentUserId , otherUserId);

        return messages.stream()
                .map(this::toResponse)
                .toList();
    }

    private ChatMessageResponse toResponse(ChatMessage message) {
        ChatMessageResponse response = new ChatMessageResponse();

        response.setId(message.getId());
        response.setSenderId(message.getSender().getId());
        response.setSenderUsername(message.getSender().getUsername());
        response.setReceiverId(message.getReceiver().getId());
        response.setCreatedAt(message.getCreatedAt());
        response.setStatus(message.getStatus());
        response.setContent(message.getContent());

        return response;
    }

    @Transactional(readOnly = true)
    public ChatHistoryResponse getConversationCursor(
            UUID currentUserId,
            UUID otherUserId,
            LocalDateTime cursorCreatedAt,
            UUID cursorId
    ) {

        List<ChatMessage> messages;

        if (cursorCreatedAt == null) {

            messages =
                    chatMessageRepository.getLatestConversation(
                            currentUserId,
                            otherUserId,
                            PageRequest.of(0, 21)
                    );

        } else {

            messages =
                    chatMessageRepository.getConversationAfterCursor(
                            currentUserId,
                            otherUserId,
                            cursorCreatedAt,
                            cursorId,
                            PageRequest.of(0, 21)
                    );
        }

        boolean hasNext = messages.size() > 20;

        if (hasNext) {
            messages.removeLast();
        }

        List<ChatMessageResponse> responses =
                messages.stream()
                        .map(this::toResponse)
                        .toList();

        ChatHistoryResponse response =
                new ChatHistoryResponse();

        response.setMessages(responses);

        response.setHasNext(hasNext);

        if (hasNext && !messages.isEmpty()) {

            ChatMessage lastMessage =
                    messages.getLast();

            response.setNextCursorCreatedAt(
                    lastMessage.getCreatedAt()
            );

            response.setNextCursorId(
                    lastMessage.getId()
            );
        }

        return response;
    }

    @Transactional
    public void markMessagesAsSeen(UUID currentUserId , UUID otherUserId){

        List<ChatMessage> messages = chatMessageRepository.findDeliveredMessagesToMarkSeen(
                currentUserId, otherUserId);

        for (ChatMessage message : messages){
            message.setStatus(MessageStatus.SEEN);
        }

        chatMessageRepository.saveAll(messages);

    }

    @Transactional(readOnly = true)
    public List<ChatConversationResponse> getConversations(UUID currentUserId) {

        List<ChatMessage> messages = chatMessageRepository.findBySenderIdOrReceiverIdOrderByCreatedAtDesc(currentUserId , currentUserId);

        Map<UUID , ChatConversationResponse> conversations = new LinkedHashMap<>();

        for (ChatMessage message : messages ){
            User otherUser;

            if (message.getSender().getId().equals(currentUserId)) {
                otherUser = message.getReceiver();
            } else  {
                otherUser = message.getSender();
            }

            if (conversations.containsKey(otherUser.getId())){
                continue;
            }

            long unreadCount = chatMessageRepository.countBySenderIdAndReceiverIdAndStatusNot(
                    otherUser.getId(),
                    currentUserId,
                    MessageStatus.SEEN
            );

            ChatConversationResponse response = new ChatConversationResponse();

            response.setUserId(otherUser.getId());
            response.setLastSeenAt(otherUser.getLastSeenAt());
            response.setOnline(otherUser.isOnline());
            response.setUnreadCount(unreadCount);
            response.setUsername(otherUser.getUsername());
            response.setProfileUrl(otherUser.getUserProfileUrl());
            response.setLastMessage(message.getContent());
            response.setLastMessageTime(message.getCreatedAt());

            if (conversations.containsKey(otherUser.getId())){
                continue;
            }
            conversations.put(otherUser.getId() ,response);
        }

        return conversations.values().stream().toList();

    }


}

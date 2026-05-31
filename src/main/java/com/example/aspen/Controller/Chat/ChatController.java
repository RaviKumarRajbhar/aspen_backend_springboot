package com.example.aspen.Controller.Chat;


import com.example.aspen.Dto.ChatHistoryResponse;
import com.example.aspen.Dto.ChatMessageResponse;
import com.example.aspen.Service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{otherUserId}")
    public ResponseEntity<ChatHistoryResponse> getConversation(@PathVariable UUID otherUserId ,
                                                               @RequestParam(required = false) LocalDateTime cursorCreatedAt,
                                                               @RequestParam(required = false) UUID cursorId ,
                                                               Authentication authentication ){
        String userIdStr = authentication.getPrincipal().toString();
        UUID currentUserId = UUID.fromString(userIdStr);

        return ResponseEntity.ok(chatService.getConversationCursor(currentUserId , otherUserId , cursorCreatedAt , cursorId));
    }
}

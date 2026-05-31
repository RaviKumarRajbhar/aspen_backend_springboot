package com.example.aspen.WebSocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatSessionManager {

    private final Map< UUID, WebSocketSession > sessions = new ConcurrentHashMap<>();

    public void addSession(UUID userId , WebSocketSession session) {
        sessions.put(userId , session);
    }

    public WebSocketSession getSession( UUID userId) {
        return sessions.get(userId);
    }

    public boolean isOnline (UUID userId ) {
        return  sessions.containsKey(userId);
    }

    public  void removeSession(UUID userId) {
        sessions.remove(userId);
    }


}

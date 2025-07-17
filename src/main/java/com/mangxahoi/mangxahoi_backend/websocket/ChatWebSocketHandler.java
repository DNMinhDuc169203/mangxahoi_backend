package com.mangxahoi.mangxahoi_backend.websocket;

import com.mangxahoi.mangxahoi_backend.dto.request.GuiTinNhanRequest;
import com.mangxahoi.mangxahoi_backend.dto.response.GuiTinNhanResponse;
import com.mangxahoi.mangxahoi_backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.util.Map;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Controller
public class ChatWebSocketHandler {
    @Autowired
    private ChatService chatService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/gui")
    @SendTo("/topic/tin-nhan")
    public GuiTinNhanResponse xuLyTinNhan(GuiTinNhanRequest request) {
        // Lưu tin nhắn vào DB và trả về cho các client
        return chatService.guiTinNhan(request);
    }

    // Xử lý exception khi gửi tin nhắn qua WebSocket
    @MessageExceptionHandler
    public void handleException(Exception exception, Principal principal, @Header("simpSessionId") String sessionId) {
        String user = (principal != null) ? principal.getName() : sessionId;
        System.out.println("Gửi lỗi về user: " + user + " - " + exception.getMessage());
        messagingTemplate.convertAndSendToUser(
            user,
            "/queue/errors",
            Map.of("type", "error", "message", exception.getMessage())
        );
    }

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getSessionId();
        System.out.println("Client connected, sessionId: " + sessionId);
    }
}

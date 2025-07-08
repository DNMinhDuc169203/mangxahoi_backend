package com.mangxahoi.mangxahoi_backend.websocket;

import com.mangxahoi.mangxahoi_backend.dto.request.GuiTinNhanRequest;
import com.mangxahoi.mangxahoi_backend.dto.response.GuiTinNhanResponse;
import com.mangxahoi.mangxahoi_backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketHandler {
    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat/gui")
    @SendTo("/topic/tin-nhan")
    public GuiTinNhanResponse xuLyTinNhan(GuiTinNhanRequest request) {
        // Lưu tin nhắn vào DB và trả về cho các client
        return chatService.guiTinNhan(request);
    }
}

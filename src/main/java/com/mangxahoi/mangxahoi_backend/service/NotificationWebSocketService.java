package com.mangxahoi.mangxahoi_backend.service;

import com.mangxahoi.mangxahoi_backend.dto.ThongBaoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationWebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationWebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotificationToUser(Integer userId, ThongBaoDTO thongBao) {
         System.out.println("Gửi notification real-time tới userId: " + userId + " - Nội dung: " + thongBao);
        messagingTemplate.convertAndSend("/topic/notification/" + userId, thongBao);
    }
} 
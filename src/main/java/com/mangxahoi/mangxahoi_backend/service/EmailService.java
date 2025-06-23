package com.mangxahoi.mangxahoi_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username:}")
    private String fromEmail;
    
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    public void guiMaXacThuc(String email, String ma, String loai) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        
        if ("QUEN_MAT_KHAU".equals(loai)) {
            message.setSubject("Mã xác thực đặt lại mật khẩu");
            message.setText("Mã xác thực để đặt lại mật khẩu của bạn là: " + ma + 
                          "\nMã có hiệu lực trong 15 phút.");
        } else {
            message.setSubject("Xác thực tài khoản");
            message.setText("Mã xác thực tài khoản của bạn là: " + ma + 
                          "\nMã có hiệu lực trong 15 phút.");
        }
        
        // Trong môi trường dev, in mã ra console thay vì gửi email
        if ("dev".equals(activeProfile)) {
            log.info("***************** MÃ XÁC THỰC *****************");
            log.info("Email: {}", email);
            log.info("Loại: {}", loai);
            log.info("Mã: {}", ma);
            log.info("***********************************************");
            return;
        }
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Không thể gửi email: {}", e.getMessage());
            log.info("***************** MÃ XÁC THỰC *****************");
            log.info("Email: {}", email);
            log.info("Loại: {}", loai);
            log.info("Mã: {}", ma);
            log.info("***********************************************");
        }
    }
} 
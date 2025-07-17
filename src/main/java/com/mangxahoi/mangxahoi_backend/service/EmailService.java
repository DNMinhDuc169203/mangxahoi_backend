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
            message.setSubject("Mã xác thực đặt lại mật khẩu - Mạng Xã Hội");
            message.setText("Xin chào!\n\n" +
                          "Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản của mình.\n\n" +
                          "Mã xác thực của bạn là: " + ma + "\n\n" +
                          "Mã này có hiệu lực trong 15 phút.\n" +
                          "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n" +
                          "Trân trọng,\n" +
                          "Đội ngũ Mạng Xã Hội");
        } else {
            message.setSubject("Xác thực tài khoản - Mạng Xã Hội");
            message.setText("Xin chào!\n\n" +
                          "Cảm ơn bạn đã đăng ký tài khoản tại Mạng Xã Hội.\n\n" +
                          "Mã xác thực tài khoản của bạn là: " + ma + "\n\n" +
                          "Mã này có hiệu lực trong 15 phút.\n" +
                          "Vui lòng nhập mã này để hoàn tất quá trình đăng ký.\n\n" +
                          "Trân trọng,\n" +
                          "Đội ngũ Mạng Xã Hội");
        }
        
        try {
            mailSender.send(message);
            log.info("✅ Email đã được gửi thành công đến: {} (Loại: {})", email, loai);
        } catch (Exception e) {
            log.error("❌ Không thể gửi email đến {}: {}", email, e.getMessage());
            
            // Fallback: in mã ra console nếu gửi email thất bại (chỉ trong dev mode)
            if ("dev".equals(activeProfile)) {
                log.info("***************** MÃ XÁC THỰC (FALLBACK) *****************");
                log.info("Email: {}", email);
                log.info("Loại: {}", loai);
                log.info("Mã: {}", ma);
                log.info("********************************************************");
            }
            
            throw new RuntimeException("Không thể gửi email: " + e.getMessage());
        }
    }
} 
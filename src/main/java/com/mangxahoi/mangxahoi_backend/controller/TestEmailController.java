package com.mangxahoi.mangxahoi_backend.controller;

import com.mangxahoi.mangxahoi_backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class TestEmailController {

    private final EmailService emailService;

    @PostMapping("/email")
    public ResponseEntity<Map<String, Object>> testEmail(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = request.get("email");
            
            if (email == null || email.trim().isEmpty()) {
                response.put("thanhCong", false);
                response.put("message", "Email không được để trống");
                return ResponseEntity.badRequest().body(response);
            }
            
            log.info("🔍 Testing email to: {}", email);
            
            // Gửi email test
            emailService.guiMaXacThuc(email, "123456", "XAC_THUC_TAI_KHOAN");
            
            response.put("thanhCong", true);
            response.put("message", "Email test đã được gửi thành công đến " + email);
            response.put("maTest", "123456");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Lỗi khi gửi email test: {}", e.getMessage(), e);
            response.put("thanhCong", false);
            response.put("message", "Lỗi khi gửi email: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 
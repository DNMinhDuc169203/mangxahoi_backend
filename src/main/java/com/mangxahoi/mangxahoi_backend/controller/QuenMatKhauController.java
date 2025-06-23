package com.mangxahoi.mangxahoi_backend.controller;

import com.mangxahoi.mangxahoi_backend.dto.request.QuenMatKhauRequest;
import com.mangxahoi.mangxahoi_backend.dto.request.XacThucMatKhauRequest;
import com.mangxahoi.mangxahoi_backend.exception.AuthException;
import com.mangxahoi.mangxahoi_backend.service.QuenMatKhauService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/quen-mat-khau")
@RequiredArgsConstructor
public class QuenMatKhauController {

    private final QuenMatKhauService quenMatKhauService;

    @PostMapping("/gui-ma")
    public ResponseEntity<Map<String, Object>> guiMaXacThuc(@Valid @RequestBody QuenMatKhauRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            quenMatKhauService.taoVaGuiMaXacThuc(request.getEmailHoacSoDienThoai());
            response.put("thanhCong", true);
            response.put("message", "Mã xác thực đã được gửi đến " + request.getEmailHoacSoDienThoai());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("thanhCong", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/dat-lai-mat-khau")
    public ResponseEntity<Map<String, Object>> datLaiMatKhau(@Valid @RequestBody XacThucMatKhauRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean ketQua = quenMatKhauService.xacThucVaDoiMatKhau(
                    request.getEmailHoacSoDienThoai(), 
                    request.getMaXacThuc(), 
                    request.getMatKhauMoi());
            
            response.put("thanhCong", ketQua);
            response.put("message", "Đặt lại mật khẩu thành công");
            return ResponseEntity.ok(response);
        } catch (AuthException e) {
            response.put("thanhCong", false);
            response.put("message", e.getMessage());
            response.put("errorCode", e.getErrorCode());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("thanhCong", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 
package com.mangxahoi.mangxahoi_backend.controller;

import com.mangxahoi.mangxahoi_backend.dto.request.GuiLaiMaXacThucRequest;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.enums.LoaiMaXacThuc;
import com.mangxahoi.mangxahoi_backend.exception.ResourceNotFoundException;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import com.mangxahoi.mangxahoi_backend.service.EmailService;
import com.mangxahoi.mangxahoi_backend.service.NguoiDungService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/xac-thuc")
@RequiredArgsConstructor
public class XacThucController {

    private final NguoiDungService nguoiDungService;
    private final NguoiDungRepository nguoiDungRepository;
    private final EmailService emailService;

    // @PostMapping("/xac-nhan")
    // public ResponseEntity<Map<String, Object>> xacNhanEmail(
    //         @RequestParam String email,
    //         @RequestParam String ma) {
    //     Map<String, Object> response = new HashMap<>();
    //     try {
    //         boolean ketQua = nguoiDungService.xacThucEmail(email, ma);
    //         response.put("thanhCong", ketQua);
    //         response.put("message", "Xác thực tài khoản thành công");
    //         return ResponseEntity.ok(response);
    //     } catch (Exception e) {
    //         response.put("thanhCong", false);
    //         response.put("message", e.getMessage());
    //         return ResponseEntity.badRequest().body(response);
    //     }
    // }
    
    @PostMapping("/gui-lai-ma")
    public ResponseEntity<Map<String, Object>> guiLaiMaXacThuc(@Valid @RequestBody GuiLaiMaXacThucRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Tìm người dùng
            NguoiDung nguoiDung = nguoiDungRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "email", request.getEmail()));
                    
            // Kiểm tra nếu người dùng đã xác thực
            if (nguoiDung.getDaXacThuc() != null && nguoiDung.getDaXacThuc()) {
                response.put("thanhCong", false);
                response.put("message", "Tài khoản đã được xác thực trước đó");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Tạo mã xác thực mới
            String maXacThuc = taoMaNgauNhien();
            
            // Lưu mã xác thực
            nguoiDung.setTokenXacThuc(maXacThuc);
            nguoiDung.setLoaiMaXacThuc(LoaiMaXacThuc.xac_thuc_tai_khoan);
            nguoiDung.setNgayCapNhat(LocalDateTime.now());
            nguoiDungRepository.save(nguoiDung);
            
            // Gửi email chứa mã xác thực
            emailService.guiMaXacThuc(request.getEmail(), maXacThuc, "XAC_THUC_TAI_KHOAN");
            
            response.put("thanhCong", true);
            response.put("message", "Mã xác thực đã được gửi lại đến " + request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("thanhCong", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Tạo mã xác thực ngẫu nhiên 6 số
     */
    private String taoMaNgauNhien() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000); // số từ 100000 đến 999999
        return String.valueOf(number);
    }
} 
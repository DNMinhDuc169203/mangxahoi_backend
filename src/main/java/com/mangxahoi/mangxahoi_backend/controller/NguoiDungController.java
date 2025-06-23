package com.mangxahoi.mangxahoi_backend.controller;

import com.mangxahoi.mangxahoi_backend.dto.request.DangNhapRequest;
import com.mangxahoi.mangxahoi_backend.dto.response.DangNhapResponse;
import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import com.mangxahoi.mangxahoi_backend.exception.AuthException;
import com.mangxahoi.mangxahoi_backend.service.NguoiDungService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/nguoi-dung")
@RequiredArgsConstructor
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;

    @PostMapping("/dang-ky")
    public ResponseEntity<Object> dangKy(@Valid @RequestBody NguoiDungDTO nguoiDungDTO) {
        try {
            NguoiDungDTO nguoiDungMoi = nguoiDungService.dangKy(nguoiDungDTO, nguoiDungDTO.getMatKhau());
            return new ResponseEntity<>(nguoiDungMoi, HttpStatus.CREATED);
        } catch (AuthException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("errorCode", e.getErrorCode());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/dang-nhap")
    public ResponseEntity<Object> dangNhap(@Valid @RequestBody DangNhapRequest request) {
        try {
            DangNhapResponse response = nguoiDungService.dangNhap(request);
            return ResponseEntity.ok(response);
        } catch (AuthException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("errorCode", e.getErrorCode());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<NguoiDungDTO> timTheoId(@PathVariable Integer id) {
        return nguoiDungService.timTheoId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NguoiDungDTO> capNhat(@PathVariable Integer id, @Valid @RequestBody NguoiDungDTO nguoiDungDTO) {
        NguoiDungDTO nguoiDungCapNhat = nguoiDungService.capNhatThongTin(id, nguoiDungDTO);
        return ResponseEntity.ok(nguoiDungCapNhat);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoa(@PathVariable Integer id) {
        nguoiDungService.xoaNguoiDung(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<NguoiDungDTO>> timTatCa(Pageable pageable) {
        Page<NguoiDungDTO> nguoiDungs = nguoiDungService.timTatCa(pageable);
        return ResponseEntity.ok(nguoiDungs);
    }

    @GetMapping("/tim-kiem")
    public ResponseEntity<Page<NguoiDungDTO>> timTheoHoTen(@RequestParam String hoTen, Pageable pageable) {
        Page<NguoiDungDTO> nguoiDungs = nguoiDungService.timTheoHoTen(hoTen, pageable);
        return ResponseEntity.ok(nguoiDungs);
    }

    @GetMapping("/xac-thuc")
    public ResponseEntity<Object> xacThuc(@RequestParam String email, @RequestParam String token) {
        try {
            boolean ketQua = nguoiDungService.xacThucEmail(email, token);
            Map<String, Object> response = new HashMap<>();
            response.put("thanhCong", ketQua);
            response.put("message", "Xác thực tài khoản thành công");
            return ResponseEntity.ok(response);
        } catch (AuthException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("thanhCong", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("errorCode", e.getErrorCode());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/dat-lai-mat-khau")
    public ResponseEntity<Object> datLaiMatKhau(@RequestBody Map<String, String> requestBody) {
        try {
            String email = requestBody.get("email");
            String matKhauMoi = requestBody.get("matKhauMoi");
            
            boolean ketQua = nguoiDungService.datLaiMatKhau(email, matKhauMoi);
            Map<String, Object> response = new HashMap<>();
            response.put("thanhCong", ketQua);
            response.put("message", "Đặt lại mật khẩu thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("thanhCong", false);
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
} 
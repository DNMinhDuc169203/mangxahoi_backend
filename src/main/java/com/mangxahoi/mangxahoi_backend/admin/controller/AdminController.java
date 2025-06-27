package com.mangxahoi.mangxahoi_backend.admin.controller;

import com.mangxahoi.mangxahoi_backend.admin.dto.request.DangNhapAdminRequest;
import com.mangxahoi.mangxahoi_backend.admin.dto.response.ThongKeResponse;
import com.mangxahoi.mangxahoi_backend.admin.service.AdminService;
import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import com.mangxahoi.mangxahoi_backend.exception.AuthException;
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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/dang-nhap")
    public ResponseEntity<Object> dangNhap(@Valid @RequestBody DangNhapAdminRequest request) {
        try {
            return ResponseEntity.ok(adminService.dangNhap(request));
        } catch (AuthException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("errorCode", e.getErrorCode());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/thong-ke")
    public ResponseEntity<ThongKeResponse> layThongKe() {
        return ResponseEntity.ok(adminService.layThongKeTongQuat());
    }

    @GetMapping("/nguoi-dung")
    public ResponseEntity<Page<NguoiDungDTO>> danhSachNguoiDung(Pageable pageable) {
        return ResponseEntity.ok(adminService.danhSachNguoiDung(pageable));
    }

    @PostMapping("/nguoi-dung/{id}/khoa")
    public ResponseEntity<Object> khoaTaiKhoan(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        adminService.khoaTaiKhoan(id, request.get("lyDo"));
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã khóa tài khoản thành công");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/nguoi-dung/{id}/mo-khoa")
    public ResponseEntity<Object> moKhoaTaiKhoan(@PathVariable Integer id) {
        adminService.moKhoaTaiKhoan(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã mở khóa tài khoản thành công");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bao-cao")
    public ResponseEntity<Object> danhSachBaoCao(
            @RequestParam(required = false) String trangThai,
            Pageable pageable
    ) {
        return ResponseEntity.ok(adminService.danhSachBaoCao(trangThai, pageable));
    }

    @PostMapping("/bao-cao/{id}/xu-ly")
    public ResponseEntity<Object> xuLyBaoCao(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request
    ) {
        adminService.xuLyBaoCao(id, request.get("trangThai"), request.get("ghiChu"));
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã xử lý báo cáo thành công");
        return ResponseEntity.ok(response);
    }
}
package com.mangxahoi.mangxahoi_backend.controller;

import com.mangxahoi.mangxahoi_backend.dto.request.DangNhapRequest;
import com.mangxahoi.mangxahoi_backend.dto.response.DangNhapResponse;
import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDungAnh;
import com.mangxahoi.mangxahoi_backend.exception.AuthException;
import com.mangxahoi.mangxahoi_backend.exception.ResourceNotFoundException;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungAnhRepository;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import com.mangxahoi.mangxahoi_backend.service.CloudinaryService;
import com.mangxahoi.mangxahoi_backend.service.NguoiDungService;
import com.mangxahoi.mangxahoi_backend.util.TokenUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/nguoi-dung")
@RequiredArgsConstructor
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;
    private final NguoiDungRepository nguoiDungRepository;
    private final NguoiDungAnhRepository nguoiDungAnhRepository;
    private final CloudinaryService cloudinaryService;
    private final TokenUtil tokenUtil;

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

    @GetMapping("/thong-tin-hien-tai")
    public ResponseEntity<Object> layThongTinHienTai(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization;
            if (authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
            }
            NguoiDungDTO nguoiDung = nguoiDungService.layThongTinHienTai(token);
            return ResponseEntity.ok(nguoiDung);
        } catch (AuthException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("errorCode", e.getErrorCode());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi khi lấy thông tin người dùng: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<NguoiDungDTO> timTheoId(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        
        String token = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
        }

        return nguoiDungService.timTheoId(id, token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NguoiDungDTO> capNhat(@PathVariable Integer id, @Valid @RequestBody NguoiDungDTO nguoiDungDTO) {
        NguoiDungDTO nguoiDungCapNhat = nguoiDungService.capNhatThongTin(id, nguoiDungDTO);
        nguoiDungCapNhat.setAnhDaiDien(null);
        return ResponseEntity.ok(nguoiDungCapNhat);
    }

    @DeleteMapping("/anh/{anhId}")
    public ResponseEntity<Object> xoaAnh(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer anhId) {
        try {
            String token = authorization.substring(7);
            NguoiDung nguoiDung = tokenUtil.layNguoiDungTuToken(token);
            nguoiDungService.xoaAnhDaiDien(nguoiDung.getId(), anhId);
            Map<String, Object> response = new HashMap<>();
            response.put("thanhCong", true);
            response.put("message", "Đã xóa ảnh thành công");
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (AuthException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("errorCode", e.getErrorCode());
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi khi xóa ảnh: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    @PostMapping("/xac-thuc")
    public ResponseEntity<Object> xacThuc(@RequestBody Map<String, String> requestBody) {
        try {
            String email = requestBody.get("email");
            String token = requestBody.get("token");
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

    @PostMapping("/anh-dai-dien")
    public ResponseEntity<Object> uploadAnhDaiDien(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "laAnhChinh", defaultValue = "true") boolean laAnhChinh) {
        try {
            String token = authorization.substring(7);
            com.mangxahoi.mangxahoi_backend.entity.NguoiDung nguoiDung = tokenUtil.layNguoiDungTuToken(token);
            Integer id = nguoiDung.getId();

            String imageUrl = nguoiDungService.uploadAnhDaiDien(id, file, laAnhChinh);

            Map<String, Object> response = new HashMap<>();
            response.put("url", imageUrl);
            response.put("laAnhChinh", laAnhChinh);

            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (IOException | RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi upload ảnh: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/doi-mat-khau")
    public ResponseEntity<Object> doiMatKhau(@RequestHeader("Authorization") String authorization,
                                             @Valid @RequestBody com.mangxahoi.mangxahoi_backend.dto.request.DoiMatKhauRequest request) {
        try {
            String token = authorization;
            if (authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
            }
            boolean ketQua = nguoiDungService.doiMatKhau(token, request.getMatKhauCu(), request.getMatKhauMoi());
            Map<String, Object> response = new HashMap<>();
            response.put("thanhCong", ketQua);
            response.put("message", "Đổi mật khẩu thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("thanhCong", false);
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/anh/{anhId}/chinh")
    public ResponseEntity<Map<String, Object>> datAnhChinh(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Integer anhId) {
        try {
            String token = authorization.substring(7);
            NguoiDung nguoiDung = tokenUtil.layNguoiDungTuToken(token);

            // Kiểm tra ảnh có tồn tại và thuộc về người dùng không
            NguoiDungAnh anh = nguoiDungAnhRepository.findById(anhId)
                    .orElseThrow(() -> new ResourceNotFoundException("Ảnh", "id", anhId));
            if (!anh.getNguoiDung().getId().equals(nguoiDung.getId())) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Ảnh không thuộc về người dùng này");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Đặt làm ảnh chính
            List<NguoiDungAnh> anhDaiDiens = nguoiDungAnhRepository.findByNguoiDung(nguoiDung);
            for (NguoiDungAnh anhDaiDien : anhDaiDiens) {
                anhDaiDien.setLaAnhChinh(anhDaiDien.getId().equals(anhId));
                nguoiDungAnhRepository.save(anhDaiDien);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("thanhCong", true);
            response.put("message", "Đã đặt ảnh làm ảnh đại diện chính");
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/anh")
    public ResponseEntity<List<NguoiDungAnh>> layDanhSachAnh(
            @RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.substring(7);
            NguoiDung nguoiDung = tokenUtil.layNguoiDungTuToken(token);
            List<NguoiDungAnh> anhDaiDiens = nguoiDungAnhRepository.findByNguoiDung(nguoiDung);
            return ResponseEntity.ok(anhDaiDiens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/anh/chinh")
    public ResponseEntity<NguoiDungAnh> layAnhChinh(
             @RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.substring(7);
            NguoiDung nguoiDung = tokenUtil.layNguoiDungTuToken(token);
            Optional<NguoiDungAnh> anhChinh = nguoiDungAnhRepository.findByNguoiDungAndLaAnhChinh(nguoiDung, true);
            return anhChinh.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/muc-rieng-tu")
    public ResponseEntity<Object> thayDoiMucRiengTu(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, String> requestBody) {
        try {
            String token = authorization;
            if (authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
            }
            String mucRiengTuStr = requestBody.get("mucRiengTu");
            com.mangxahoi.mangxahoi_backend.enums.CheDoBaiViet cheDoMoi = 
                com.mangxahoi.mangxahoi_backend.enums.CheDoBaiViet.valueOf(mucRiengTuStr);

            NguoiDungDTO updatedNguoiDung = nguoiDungService.thayDoiMucRiengTu(token, cheDoMoi);
            return ResponseEntity.ok(updatedNguoiDung);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Giá trị 'mucRiengTu' không hợp lệ. Vui lòng sử dụng: cong_khai, ban_be, rieng_tu");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi khi thay đổi mức riêng tư: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 
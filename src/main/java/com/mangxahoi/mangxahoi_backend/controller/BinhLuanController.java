package com.mangxahoi.mangxahoi_backend.controller;

import com.mangxahoi.mangxahoi_backend.dto.BinhLuanDTO;
import com.mangxahoi.mangxahoi_backend.entity.BaiViet;
import com.mangxahoi.mangxahoi_backend.entity.BinhLuan;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.exception.ResourceNotFoundException;
import com.mangxahoi.mangxahoi_backend.repository.BaiVietRepository;
import com.mangxahoi.mangxahoi_backend.repository.BinhLuanRepository;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import com.mangxahoi.mangxahoi_backend.service.BinhLuanService;
import com.mangxahoi.mangxahoi_backend.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/binh-luan")
@RequiredArgsConstructor
public class BinhLuanController {

    private final BinhLuanService binhLuanService;
    private final BinhLuanRepository binhLuanRepository;
    private final BaiVietRepository baiVietRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final TokenUtil tokenUtil;

    private NguoiDung getUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SecurityException("Invalid or missing Authorization header");
        }
        String token = authHeader.substring(7);
        return tokenUtil.layNguoiDungTuToken(token);
    }

    /**
     * Thêm bình luận mới cho bài viết
     * 
     * @param idBaiViet ID của bài viết
     * @param authorization Token xác thực
     * @param noiDung Nội dung bình luận
     * @return Thông tin bình luận đã thêm
     */
    @PostMapping("/bai-viet/{idBaiViet}")
    public ResponseEntity<BinhLuanDTO> themBinhLuan(
            @PathVariable Integer idBaiViet,
            @RequestHeader("Authorization") String authorization,
            @RequestParam String noiDung) {
        
        try {
            NguoiDung nguoiDung = getUserFromToken(authorization);
            
            // Tạo DTO cho bình luận
            BinhLuanDTO binhLuanDTO = new BinhLuanDTO();
            binhLuanDTO.setNoiDung(noiDung);
            
            // Thêm bình luận
            BinhLuanDTO createdBinhLuan = binhLuanService.themBinhLuan(idBaiViet, nguoiDung.getId(), null, binhLuanDTO);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBinhLuan);
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Thêm bình luận phản hồi cho bình luận khác
     * 
     * @param idBaiViet ID của bài viết
     * @param idBinhLuanCha ID của bình luận cha
     * @param authorization Token xác thực
     * @param noiDung Nội dung bình luận
     * @return Thông tin bình luận đã thêm
     */
    @PostMapping("/bai-viet/{idBaiViet}/binh-luan/{idBinhLuanCha}")
    public ResponseEntity<BinhLuanDTO> themBinhLuanPhanHoi(
            @PathVariable Integer idBaiViet,
            @PathVariable Integer idBinhLuanCha,
            @RequestHeader("Authorization") String authorization,
            @RequestParam String noiDung) {
        
        try {
            NguoiDung nguoiDung = getUserFromToken(authorization);
            
            // Kiểm tra bình luận cha tồn tại
            BinhLuan binhLuanCha = binhLuanRepository.findById(idBinhLuanCha)
                    .orElseThrow(() -> new ResourceNotFoundException("Bình luận", "id", idBinhLuanCha));
            
            // Kiểm tra bình luận cha thuộc bài viết
            if (!binhLuanCha.getBaiViet().getId().equals(idBaiViet)) {
                return ResponseEntity.badRequest().build();
            }
            
            // Tạo DTO cho bình luận
            BinhLuanDTO binhLuanDTO = new BinhLuanDTO();
            binhLuanDTO.setNoiDung(noiDung);
            
            // Thêm bình luận phản hồi
            BinhLuanDTO createdBinhLuan = binhLuanService.themBinhLuan(idBaiViet, nguoiDung.getId(), idBinhLuanCha, binhLuanDTO);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBinhLuan);
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Cập nhật bình luận
     * 
     * @param id ID của bình luận
     * @param authorization Token xác thực
     * @param noiDung Nội dung bình luận mới
     * @return Thông tin bình luận đã cập nhật
     */
    @PutMapping("/{id}")
    public ResponseEntity<BinhLuanDTO> capNhatBinhLuan(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String authorization,
            @RequestParam String noiDung) {
        
        try {
            NguoiDung nguoiDung = getUserFromToken(authorization);
            
            // Tạo DTO cho bình luận
            BinhLuanDTO binhLuanDTO = new BinhLuanDTO();
            binhLuanDTO.setNoiDung(noiDung);
            
            // Cập nhật bình luận
            BinhLuanDTO updatedBinhLuan = binhLuanService.capNhatBinhLuan(id, nguoiDung.getId(), binhLuanDTO);
            
            return ResponseEntity.ok(updatedBinhLuan);
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Xóa bình luận
     * 
     * @param id ID của bình luận
     * @param authorization Token xác thực
     * @return Kết quả xóa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> xoaBinhLuan(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String authorization) {
        
        try {
            NguoiDung nguoiDung = getUserFromToken(authorization);
            
            // Xóa bình luận
            boolean result = binhLuanService.xoaBinhLuan(id, nguoiDung.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("thanhCong", result);
            response.put("message", result ? "Đã xóa bình luận thành công" : "Không thể xóa bình luận");
            
            return ResponseEntity.ok(response);
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("thanhCong", false);
            response.put("message", "Lỗi khi xóa bình luận: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Lấy danh sách bình luận của bài viết (yêu cầu token)
     */
    @GetMapping("/bai-viet/{idBaiViet}")
    public ResponseEntity<Map<String, Object>> layBinhLuanTheoBaiViet(
            @PathVariable Integer idBaiViet,
            @RequestHeader("Authorization") String authorization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        NguoiDung nguoiDung = getUserFromToken(authorization);
        try {
            // Kiểm tra bài viết tồn tại
            BaiViet baiViet = baiVietRepository.findById(idBaiViet)
                    .orElseThrow(() -> new ResourceNotFoundException("Bài viết", "id", idBaiViet));
            Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());
            Page<BinhLuanDTO> binhLuanPage = binhLuanService.layBinhLuanGocTheoBaiViet(idBaiViet, pageable);
            Map<String, Object> response = new HashMap<>();
            response.put("binhLuan", binhLuanPage.getContent());
            response.put("trangHienTai", binhLuanPage.getNumber());
            response.put("tongSoTrang", binhLuanPage.getTotalPages());
            response.put("tongSoBinhLuan", binhLuanPage.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lấy danh sách bình luận phản hồi của một bình luận (yêu cầu token)
     */
    @GetMapping("/{idBinhLuanCha}/phan-hoi")
    public ResponseEntity<Map<String, Object>> layBinhLuanPhanHoi(
            @PathVariable Integer idBinhLuanCha,
            @RequestHeader("Authorization") String authorization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        NguoiDung nguoiDung = getUserFromToken(authorization);
        try {
            // Kiểm tra bình luận cha tồn tại
            BinhLuan binhLuanCha = binhLuanRepository.findById(idBinhLuanCha)
                    .orElseThrow(() -> new ResourceNotFoundException("Bình luận", "id", idBinhLuanCha));
            Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").ascending());
            Page<BinhLuanDTO> binhLuanPage = binhLuanService.layBinhLuanPhanHoi(idBinhLuanCha, pageable);
            Map<String, Object> response = new HashMap<>();
            response.put("binhLuan", binhLuanPage.getContent());
            response.put("trangHienTai", binhLuanPage.getNumber());
            response.put("tongSoTrang", binhLuanPage.getTotalPages());
            response.put("tongSoBinhLuan", binhLuanPage.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Thích bình luận
     * 
     * @param idBinhLuan ID của bình luận
     * @param authorization Token xác thực
     * @return Kết quả thích
     */
    @PostMapping("/{idBinhLuan}/thich")
    public ResponseEntity<Map<String, Object>> thichBinhLuan(
            @PathVariable Integer idBinhLuan,
            @RequestHeader("Authorization") String authorization) {
        
        try {
            NguoiDung nguoiDung = getUserFromToken(authorization);
            boolean result = binhLuanService.thichBinhLuan(idBinhLuan, nguoiDung.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("thanhCong", result);
            response.put("message", result ? "Đã thích bình luận" : "Không thể thích bình luận");
            
            return ResponseEntity.ok(response);
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("thanhCong", false);
            response.put("message", "Lỗi khi thích bình luận: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Bỏ thích bình luận
     * 
     * @param idBinhLuan ID của bình luận
     * @param authorization Token xác thực
     * @return Kết quả bỏ thích
     */
    @DeleteMapping("/{idBinhLuan}/thich")
    public ResponseEntity<Map<String, Object>> boThichBinhLuan(
            @PathVariable Integer idBinhLuan,
            @RequestHeader("Authorization") String authorization) {
        
        try {
            NguoiDung nguoiDung = getUserFromToken(authorization);
            boolean result = binhLuanService.boThichBinhLuan(idBinhLuan, nguoiDung.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("thanhCong", result);
            response.put("message", result ? "Đã bỏ thích bình luận" : "Không thể bỏ thích bình luận");
            
            return ResponseEntity.ok(response);
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("thanhCong", false);
            response.put("message", "Lỗi khi bỏ thích bình luận: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 
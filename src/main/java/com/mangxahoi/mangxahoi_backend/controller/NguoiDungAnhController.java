package com.mangxahoi.mangxahoi_backend.controller;

import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDungAnh;
import com.mangxahoi.mangxahoi_backend.exception.ResourceNotFoundException;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungAnhRepository;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import com.mangxahoi.mangxahoi_backend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/nguoi-dung-anh")
@RequiredArgsConstructor
public class NguoiDungAnhController {

    private final NguoiDungRepository nguoiDungRepository;
    private final NguoiDungAnhRepository nguoiDungAnhRepository;
    private final CloudinaryService cloudinaryService;

    /**
     * Upload ảnh đại diện cho người dùng
     * 
     * @param id ID của người dùng
     * @param file File ảnh
     * @param laAnhChinh Có phải là ảnh đại diện chính không
     * @return Thông tin về ảnh đã upload
     */
    @PostMapping("/{id}/upload")
    public ResponseEntity<Map<String, Object>> uploadAnhDaiDien(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "laAnhChinh", defaultValue = "false") Boolean laAnhChinh) {
        
        try {
            // Kiểm tra người dùng tồn tại
            NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", id));
            
            // Upload ảnh lên Cloudinary
            String imageUrl = cloudinaryService.uploadFile(file, "nguoi_dung_anh");
            
            // Nếu là ảnh chính, cập nhật các ảnh khác thành không phải ảnh chính
            if (laAnhChinh) {
                List<NguoiDungAnh> anhDaiDiens = nguoiDungAnhRepository.findByNguoiDung(nguoiDung);
                for (NguoiDungAnh anh : anhDaiDiens) {
                    if (anh.getLaAnhChinh()) {
                        anh.setLaAnhChinh(false);
                        nguoiDungAnhRepository.save(anh);
                    }
                }
            }
            
            // Lưu thông tin ảnh vào database
            NguoiDungAnh anhDaiDien = NguoiDungAnh.builder()
                    .nguoiDung(nguoiDung)
                    .url(imageUrl)
                    .laAnhChinh(laAnhChinh)
                    .build();
            
            NguoiDungAnh savedAnh = nguoiDungAnhRepository.save(anhDaiDien);
            
            // Trả về kết quả
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedAnh.getId());
            response.put("url", savedAnh.getUrl());
            response.put("laAnhChinh", savedAnh.getLaAnhChinh());
            response.put("ngayTao", savedAnh.getNgayTao());
            
            return ResponseEntity.ok(response);
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Lỗi khi upload ảnh: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Đặt ảnh làm ảnh đại diện chính
     * 
     * @param nguoiDungId ID của người dùng
     * @param anhId ID của ảnh
     * @return Kết quả cập nhật
     */
    @PutMapping("/{nguoiDungId}/anh/{anhId}/chinh")
    public ResponseEntity<Map<String, Object>> datAnhChinh(
            @PathVariable Integer nguoiDungId,
            @PathVariable Integer anhId) {
        
        try {
            // Kiểm tra người dùng tồn tại
            NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                    .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", nguoiDungId));
            
            // Kiểm tra ảnh tồn tại
            NguoiDungAnh anh = nguoiDungAnhRepository.findById(anhId)
                    .orElseThrow(() -> new ResourceNotFoundException("Ảnh", "id", anhId));
            
            // Kiểm tra ảnh có thuộc về người dùng không
            if (!anh.getNguoiDung().getId().equals(nguoiDungId)) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Ảnh không thuộc về người dùng này");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // Cập nhật các ảnh khác thành không phải ảnh chính
            List<NguoiDungAnh> anhDaiDiens = nguoiDungAnhRepository.findByNguoiDung(nguoiDung);
            for (NguoiDungAnh anhDaiDien : anhDaiDiens) {
                if (anhDaiDien.getLaAnhChinh()) {
                    anhDaiDien.setLaAnhChinh(false);
                    nguoiDungAnhRepository.save(anhDaiDien);
                }
            }
            
            // Đặt ảnh hiện tại làm ảnh chính
            anh.setLaAnhChinh(true);
            nguoiDungAnhRepository.save(anh);
            
            // Trả về kết quả
            Map<String, Object> response = new HashMap<>();
            response.put("thanhCong", true);
            response.put("message", "Đã đặt ảnh làm ảnh đại diện chính");
            
            return ResponseEntity.ok(response);
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Xóa ảnh
     * 
     * @param nguoiDungId ID của người dùng
     * @param anhId ID của ảnh
     * @return Kết quả xóa
     */
    @DeleteMapping("/{nguoiDungId}/anh/{anhId}")
    public ResponseEntity<Map<String, Object>> xoaAnh(
            @PathVariable Integer nguoiDungId,
            @PathVariable Integer anhId) {
        
        try {
            // Kiểm tra người dùng tồn tại
            nguoiDungRepository.findById(nguoiDungId)
                    .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", nguoiDungId));
            
            // Kiểm tra ảnh tồn tại
            NguoiDungAnh anh = nguoiDungAnhRepository.findById(anhId)
                    .orElseThrow(() -> new ResourceNotFoundException("Ảnh", "id", anhId));
            
            // Kiểm tra ảnh có thuộc về người dùng không
            if (!anh.getNguoiDung().getId().equals(nguoiDungId)) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Ảnh không thuộc về người dùng này");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // Không cho phép xóa ảnh chính nếu chỉ có một ảnh
            if (anh.getLaAnhChinh()) {
                List<NguoiDungAnh> anhDaiDiens = nguoiDungAnhRepository.findByNguoiDung(anh.getNguoiDung());
                if (anhDaiDiens.size() <= 1) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("error", "Không thể xóa ảnh đại diện duy nhất");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }
            
            // Xóa ảnh trên Cloudinary
            try {
                // Lấy public_id từ URL
                String url = anh.getUrl();
                String publicId = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
                
                // Xóa ảnh trên Cloudinary
                cloudinaryService.deleteFile("nguoi_dung_anh/" + publicId);
            } catch (IOException e) {
                // Ghi log lỗi nhưng vẫn tiếp tục xóa trong database
                System.err.println("Lỗi khi xóa ảnh trên Cloudinary: " + e.getMessage());
            }
            
            // Xóa ảnh trong database
            nguoiDungAnhRepository.delete(anh);
            
            // Trả về kết quả
            Map<String, Object> response = new HashMap<>();
            response.put("thanhCong", true);
            response.put("message", "Đã xóa ảnh thành công");
            
            return ResponseEntity.ok(response);
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Lấy danh sách ảnh của người dùng
     * 
     * @param nguoiDungId ID của người dùng
     * @return Danh sách ảnh
     */
    @GetMapping("/{nguoiDungId}")
    public ResponseEntity<List<NguoiDungAnh>> layDanhSachAnh(@PathVariable Integer nguoiDungId) {
        try {
            // Kiểm tra người dùng tồn tại
            NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                    .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", nguoiDungId));
            
            // Lấy danh sách ảnh
            List<NguoiDungAnh> anhDaiDiens = nguoiDungAnhRepository.findByNguoiDung(nguoiDung);
            
            return ResponseEntity.ok(anhDaiDiens);
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Lấy ảnh đại diện chính của người dùng
     * 
     * @param nguoiDungId ID của người dùng
     * @return Ảnh đại diện chính
     */
    @GetMapping("/{nguoiDungId}/anh-chinh")
    public ResponseEntity<NguoiDungAnh> layAnhChinh(@PathVariable Integer nguoiDungId) {
        try {
            // Kiểm tra người dùng tồn tại
            NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                    .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", nguoiDungId));
            
            // Lấy ảnh chính
            Optional<NguoiDungAnh> anhChinh = nguoiDungAnhRepository.findByNguoiDungAndLaAnhChinh(nguoiDung, true);
            
            return anhChinh.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 
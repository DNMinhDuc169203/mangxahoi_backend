package com.mangxahoi.mangxahoi_backend.controller;

import com.mangxahoi.mangxahoi_backend.dto.BaiVietDTO;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.exception.ResourceNotFoundException;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import com.mangxahoi.mangxahoi_backend.service.BaiVietService;
import com.mangxahoi.mangxahoi_backend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bai-viet")
@RequiredArgsConstructor
public class BaiVietController {

    private final BaiVietService baiVietService;
    private final NguoiDungRepository nguoiDungRepository;
    private final CloudinaryService cloudinaryService;

    /**
     * Tạo bài viết mới
     * 
     * @param idNguoiDung ID của người dùng
     * @param noiDung Nội dung bài viết
     * @param cheDoRiengTu Chế độ riêng tư (cong_khai, ban_be, rieng_tu)
     * @param media Danh sách file media đính kèm
     * @param hashtags Danh sách hashtag (phân cách bởi dấu phẩy, có thể có hoặc không có dấu # ở đầu)
     * @return Thông tin bài viết đã tạo
     */
    @PostMapping("/{idNguoiDung}")
    public ResponseEntity<BaiVietDTO> taoBaiViet(
            @PathVariable Integer idNguoiDung,
            @RequestParam("noiDung") String noiDung,
            @RequestParam(value = "cheDoRiengTu", defaultValue = "cong_khai") String cheDoRiengTu,
            @RequestParam(value = "media", required = false) List<MultipartFile> media,
            @RequestParam(value = "hashtags", required = false) String hashtags) {
        
        try {
            // Kiểm tra người dùng tồn tại
            NguoiDung nguoiDung = nguoiDungRepository.findById(idNguoiDung)
                    .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", idNguoiDung));
            
            // Tạo DTO cho bài viết
            BaiVietDTO baiVietDTO = new BaiVietDTO();
            baiVietDTO.setNoiDung(noiDung);
            baiVietDTO.setCheDoRiengTu(com.mangxahoi.mangxahoi_backend.enums.CheDoBaiViet.valueOf(cheDoRiengTu));
            
            // Xử lý hashtags nếu có (ví dụ: "mangxahoi,baivietdautien" hoặc "#mangxahoi,#baivietdautien")
            if (hashtags != null && !hashtags.isEmpty()) {
                List<String> hashtagList = List.of(hashtags.split(","));
                baiVietDTO.setHashtags(hashtagList);
            }
            
            // Tạo bài viết
            BaiVietDTO createdBaiViet = baiVietService.taoBaiViet(idNguoiDung, baiVietDTO, media);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBaiViet);
            
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // In ra lỗi chi tiết để debug
            e.printStackTrace();
            
            // Tạo response với thông báo lỗi
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("cause", e.getCause() != null ? e.getCause().getMessage() : "Unknown cause");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * Cập nhật bài viết
     * 
     * @param id ID của bài viết
     * @param baiVietDTO Thông tin bài viết cần cập nhật
     * @return Thông tin bài viết đã cập nhật
     */
    @PutMapping("/{id}")
    public ResponseEntity<BaiVietDTO> capNhatBaiViet(
            @PathVariable Integer id,
            @RequestBody BaiVietDTO baiVietDTO) {
        
        try {
            BaiVietDTO updatedBaiViet = baiVietService.capNhatBaiViet(id, baiVietDTO);
            return ResponseEntity.ok(updatedBaiViet);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Xóa bài viết
     * 
     * @param id ID của bài viết
     * @return Kết quả xóa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> xoaBaiViet(@PathVariable Integer id) {
        try {
            baiVietService.xoaBaiViet(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("thanhCong", true);
            response.put("message", "Đã xóa bài viết thành công");
            
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("thanhCong", false);
            response.put("message", "Lỗi khi xóa bài viết: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Lấy thông tin bài viết theo ID
     * 
     * @param id ID của bài viết
     * @param idNguoiDungHienTai ID của người dùng hiện tại
     * @return Thông tin bài viết
     */
    @GetMapping("/{id}")
    public ResponseEntity<BaiVietDTO> layBaiVietTheoId(
            @PathVariable Integer id,
            @RequestParam Integer idNguoiDungHienTai) {
        
        try {
            Optional<BaiVietDTO> baiViet = baiVietService.timTheoId(id, idNguoiDungHienTai);
            
            return baiViet.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lấy danh sách bài viết của người dùng
     * 
     * @param idNguoiDung ID của người dùng
     * @param idNguoiDungHienTai ID của người dùng hiện tại
     * @param page Số trang
     * @param size Số lượng mỗi trang
     * @return Danh sách bài viết
     */
    @GetMapping("/nguoi-dung/{idNguoiDung}")
    public ResponseEntity<Map<String, Object>> layBaiVietCuaNguoiDung(
            @PathVariable Integer idNguoiDung,
            @RequestParam Integer idNguoiDungHienTai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());
            Page<BaiVietDTO> baiVietPage = baiVietService.timBaiVietCuaNguoiDung(idNguoiDung, idNguoiDungHienTai, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("baiViet", baiVietPage.getContent());
            response.put("trangHienTai", baiVietPage.getNumber());
            response.put("tongSoTrang", baiVietPage.getTotalPages());
            response.put("tongSoBaiViet", baiVietPage.getTotalElements());
            
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lấy danh sách bài viết công khai
     * 
     * @param idNguoiDungHienTai ID của người dùng hiện tại
     * @param page Số trang
     * @param size Số lượng mỗi trang
     * @return Danh sách bài viết công khai
     */
    @GetMapping("/cong-khai")
    public ResponseEntity<Map<String, Object>> layBaiVietCongKhai(
            @RequestParam Integer idNguoiDungHienTai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());
            Page<BaiVietDTO> baiVietPage = baiVietService.timBaiVietCongKhai(idNguoiDungHienTai, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("baiViet", baiVietPage.getContent());
            response.put("trangHienTai", baiVietPage.getNumber());
            response.put("tongSoTrang", baiVietPage.getTotalPages());
            response.put("tongSoBaiViet", baiVietPage.getTotalElements());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lấy danh sách bài viết xu hướng
     * 
     * @param idNguoiDungHienTai ID của người dùng hiện tại
     * @param page Số trang
     * @param size Số lượng mỗi trang
     * @return Danh sách bài viết xu hướng
     */
    @GetMapping("/xu-huong")
    public ResponseEntity<Map<String, Object>> layBaiVietXuHuong(
            @RequestParam Integer idNguoiDungHienTai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BaiVietDTO> baiVietPage = baiVietService.timBaiVietXuHuong(idNguoiDungHienTai, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("baiViet", baiVietPage.getContent());
            response.put("trangHienTai", baiVietPage.getNumber());
            response.put("tongSoTrang", baiVietPage.getTotalPages());
            response.put("tongSoBaiViet", baiVietPage.getTotalElements());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lấy danh sách bài viết theo hashtag
     * 
     * @param tenHashtag Tên hashtag
     * @param idNguoiDungHienTai ID của người dùng hiện tại
     * @param page Số trang
     * @param size Số lượng mỗi trang
     * @return Danh sách bài viết có hashtag
     */
    @GetMapping("/hashtag/{tenHashtag}")
    public ResponseEntity<Map<String, Object>> layBaiVietTheoHashtag(
            @PathVariable String tenHashtag,
            @RequestParam Integer idNguoiDungHienTai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());
            Page<BaiVietDTO> baiVietPage = baiVietService.timBaiVietTheoHashtag(tenHashtag, idNguoiDungHienTai, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("baiViet", baiVietPage.getContent());
            response.put("trangHienTai", baiVietPage.getNumber());
            response.put("tongSoTrang", baiVietPage.getTotalPages());
            response.put("tongSoBaiViet", baiVietPage.getTotalElements());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Thích bài viết
     * 
     * @param idBaiViet ID của bài viết
     * @param idNguoiDung ID của người dùng
     * @return Kết quả thích
     */
    @PostMapping("/{idBaiViet}/thich")
    public ResponseEntity<Map<String, Object>> thichBaiViet(
            @PathVariable Integer idBaiViet,
            @RequestParam Integer idNguoiDung) {
        
        try {
            boolean result = baiVietService.thichBaiViet(idBaiViet, idNguoiDung);
            
            Map<String, Object> response = new HashMap<>();
            response.put("thanhCong", result);
            response.put("message", result ? "Đã thích bài viết" : "Không thể thích bài viết");
            
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("thanhCong", false);
            response.put("message", "Lỗi khi thích bài viết: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Bỏ thích bài viết
     * 
     * @param idBaiViet ID của bài viết
     * @param idNguoiDung ID của người dùng
     * @return Kết quả bỏ thích
     */
    @DeleteMapping("/{idBaiViet}/thich")
    public ResponseEntity<Map<String, Object>> boThichBaiViet(
            @PathVariable Integer idBaiViet,
            @RequestParam Integer idNguoiDung) {
        
        try {
            boolean result = baiVietService.boThichBaiViet(idBaiViet, idNguoiDung);
            
            Map<String, Object> response = new HashMap<>();
            response.put("thanhCong", result);
            response.put("message", result ? "Đã bỏ thích bài viết" : "Không thể bỏ thích bài viết");
            
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("thanhCong", false);
            response.put("message", "Lỗi khi bỏ thích bài viết: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 
package com.mangxahoi.mangxahoi_backend.admin.controller;

import com.mangxahoi.mangxahoi_backend.admin.dto.request.DangNhapAdminRequest;
import com.mangxahoi.mangxahoi_backend.admin.dto.request.ThemViPhamRequest;
import com.mangxahoi.mangxahoi_backend.admin.dto.response.LichSuViPhamDTO;
import com.mangxahoi.mangxahoi_backend.admin.dto.response.ThongKeResponse;
import com.mangxahoi.mangxahoi_backend.admin.dto.response.ThongTinViPhamNguoiDungDTO;
import com.mangxahoi.mangxahoi_backend.admin.service.AdminService;
import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import com.mangxahoi.mangxahoi_backend.exception.AuthException;
import com.mangxahoi.mangxahoi_backend.service.BaiVietService;
import com.mangxahoi.mangxahoi_backend.entity.LichSuXuLyBaiViet;
import com.mangxahoi.mangxahoi_backend.entity.ChinhSach;
import com.mangxahoi.mangxahoi_backend.admin.service.ChinhSachService;
import com.mangxahoi.mangxahoi_backend.dto.BaiVietDTO;
import com.mangxahoi.mangxahoi_backend.admin.dto.response.BaoCaoDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mangxahoi.mangxahoi_backend.entity.BaoCao;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final BaiVietService baiVietService;
    private final ChinhSachService chinhSachService;

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

    @GetMapping("/nguoi-dung/{id}/lich-su-vi-pham")
    public ResponseEntity<List<LichSuViPhamDTO>> lichSuViPhamNguoiDung(@PathVariable Integer id) {
        return ResponseEntity.ok(adminService.lichSuViPhamNguoiDung(id));
    }

    @PostMapping("/nguoi-dung/them-vi-pham")
    public ResponseEntity<LichSuViPhamDTO> themViPhamNguoiDung(@RequestBody ThemViPhamRequest request, @RequestParam Integer adminId) {
        return ResponseEntity.ok(adminService.themViPhamNguoiDung(request, adminId));
    }

    @GetMapping("/nguoi-dung/{id}/thong-tin-vi-pham")
    public ResponseEntity<ThongTinViPhamNguoiDungDTO> thongTinViPhamNguoiDung(@PathVariable Integer id) {
        return ResponseEntity.ok(adminService.thongTinViPhamNguoiDung(id));
    }

    @PostMapping("/bai-viet/{id}/an")
    public ResponseEntity<?> anBaiVietByAdmin(@PathVariable Integer id, @RequestParam Integer adminId, @RequestBody Map<String, String> body) {
        String lyDo = body.getOrDefault("lyDo", "");
        baiVietService.anBaiVietByAdmin(id, adminId, lyDo);
        return ResponseEntity.ok(Map.of("message", "Đã ẩn bài viết thành công"));
    }

    @PostMapping("/bai-viet/{id}/hien")
    public ResponseEntity<?> hienBaiVietByAdmin(@PathVariable Integer id, @RequestParam Integer adminId) {
        baiVietService.hienBaiVietByAdmin(id, adminId);
        return ResponseEntity.ok(Map.of("message", "Đã khôi phục bài viết thành công"));
    }

    @DeleteMapping("/bai-viet/{id}")
    public ResponseEntity<?> xoaBaiVietByAdmin(@PathVariable Integer id, @RequestParam Integer adminId, @RequestBody(required = false) Map<String, String> body) {
        String lyDo = (body != null) ? body.getOrDefault("lyDo", "") : "";
        baiVietService.xoaBaiVietByAdmin(id, adminId, lyDo);
        return ResponseEntity.ok(Map.of("message", "Đã xóa bài viết thành công"));
    }

    @GetMapping("/bai-viet/tim-kiem")
    public ResponseEntity<Page<BaiVietDTO>> timKiemBaiVietAdmin(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String hashtag,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String loai,
            @RequestParam(required = false) Boolean sensitive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<BaiVietDTO> result = baiVietService.timKiemBaiVietAdmin(keyword, hashtag, trangThai, loai, sensitive, PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/bai-viet/{id}/lich-su-xu-ly")
    public ResponseEntity<List<LichSuXuLyBaiViet>> lichSuXuLyBaiViet(@PathVariable Integer id) {
        return ResponseEntity.ok(baiVietService.lichSuXuLyBaiViet(id));
    }

    @GetMapping("/bai-viet/thong-ke")
    public ResponseEntity<Map<String, Object>> thongKeBaiViet(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) {
        return ResponseEntity.ok(baiVietService.thongKeBaiViet(fromDate, toDate));
    }

    @GetMapping("/bai-viet/moi-nhat")
    public ResponseEntity<List<BaiVietDTO>> layBaiVietMoiNhat() {
        List<BaiVietDTO> result = baiVietService.findTop5MoiNhat();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/bao-cao/moi-nhat")
    public ResponseEntity<List<BaoCaoDTO>> layBaoCaoMoiNhat() {
        List<BaoCaoDTO> result = adminService.findTop5BaoCaoMoiNhat();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/chinh-sach")
    public ResponseEntity<ChinhSach> taoChinhSach(@RequestBody Map<String, String> body, @RequestParam Integer adminId) {
        String tieuDe = body.getOrDefault("tieuDe", "");
        String noiDung = body.getOrDefault("noiDung", "");
        return ResponseEntity.ok(chinhSachService.taoChinhSach(tieuDe, noiDung, adminId));
    }

    @PutMapping("/chinh-sach/{id}")
    public ResponseEntity<ChinhSach> capNhatChinhSach(@PathVariable Integer id, @RequestBody Map<String, String> body, @RequestParam Integer adminId) {
        String tieuDe = body.getOrDefault("tieuDe", "");
        String noiDung = body.getOrDefault("noiDung", "");
        return ResponseEntity.ok(chinhSachService.capNhatChinhSach(id, tieuDe, noiDung, adminId));
    }

    @GetMapping("/chinh-sach")
    public ResponseEntity<List<ChinhSach>> layDanhSachChinhSach() {
        return ResponseEntity.ok(chinhSachService.layDanhSachChinhSach());
    }

    @GetMapping("/chinh-sach/{id}")
    public ResponseEntity<ChinhSach> layChiTietChinhSach(@PathVariable Integer id) {
        return ResponseEntity.ok(chinhSachService.layChiTietChinhSach(id));
    }
}
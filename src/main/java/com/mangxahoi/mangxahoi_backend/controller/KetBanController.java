package com.mangxahoi.mangxahoi_backend.controller;

import com.mangxahoi.mangxahoi_backend.service.KetBanService;
import com.mangxahoi.mangxahoi_backend.service.ThongBaoService;
import com.mangxahoi.mangxahoi_backend.util.TokenUtil;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mangxahoi.mangxahoi_backend.dto.LoiMoiKetBanDaGuiDTO;
import com.mangxahoi.mangxahoi_backend.dto.LoiMoiKetBanDaNhanDTO;
import org.springframework.http.HttpStatus;

import java.util.Map;

@RestController
@RequestMapping("/api/ket-ban")
@RequiredArgsConstructor
public class KetBanController {

    private final KetBanService ketBanService;
    private final ThongBaoService thongBaoService;
    private final TokenUtil tokenUtil;

    private Integer getUserIdFromToken(String authHeader) {
        String token = authHeader.substring(7);
        return tokenUtil.layNguoiDungTuToken(token).getId();
    }

    @PostMapping("/loi-moi/{idNguoiNhan}")
    public ResponseEntity<Map<String, String>> guiLoiMoiKetBan(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer idNguoiNhan) {
        Integer idNguoiGui = getUserIdFromToken(authHeader);
        Integer idKetBan = ketBanService.guiLoiMoiKetBan(idNguoiGui, idNguoiNhan);
        
        // Gửi thông báo cho người nhận
        if (idKetBan != null) {
            thongBaoService.guiThongBaoLoiMoiKetBan(idNguoiGui, idKetBan);
        }
        
        return ResponseEntity.ok(Map.of("message", "Gửi lời mời kết bạn thành công."));
    }

    @PostMapping("/chap-nhan/{idLoiMoi}")
    public ResponseEntity<Map<String, String>> chapNhanLoiMoiKetBan(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer idLoiMoi) {
        Integer idNguoiDung = getUserIdFromToken(authHeader);
        ketBanService.chapNhanLoiMoiKetBan(idNguoiDung, idLoiMoi);
        return ResponseEntity.ok(Map.of("message", "Chấp nhận lời mời kết bạn thành công."));
    }

    @DeleteMapping("/tu-choi/{idLoiMoi}")
    public ResponseEntity<Map<String, String>> tuChoiLoiMoiKetBan(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer idLoiMoi) {
        Integer idNguoiDung = getUserIdFromToken(authHeader);
        ketBanService.tuChoiLoiMoiKetBan(idNguoiDung, idLoiMoi);
        return ResponseEntity.ok(Map.of("message", "Từ chối lời mời kết bạn thành công."));
    }

    @DeleteMapping("/huy-loi-moi/{idLoiMoi}")
    public ResponseEntity<Map<String, String>> huyLoiMoiKetBan(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer idLoiMoi) {
        Integer idNguoiDung = getUserIdFromToken(authHeader);
        ketBanService.huyLoiMoiKetBan(idNguoiDung, idLoiMoi);
        return ResponseEntity.ok(Map.of("message", "Hủy lời mời đã gửi thành công."));
    }

    @DeleteMapping("/ban-be/{idBanBe}")
    public ResponseEntity<Map<String, String>> huyKetBan(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer idBanBe) {
        Integer idNguoiHuy = getUserIdFromToken(authHeader);
        ketBanService.huyKetBan(idNguoiHuy, idBanBe);
        return ResponseEntity.ok(Map.of("message", "Hủy kết bạn thành công."));
    }

    @PostMapping("/chan/{idNguoiBiChan}")
    public ResponseEntity<Map<String, String>> chanNguoiDung(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer idNguoiBiChan) {
        Integer idNguoiChan = getUserIdFromToken(authHeader);
        ketBanService.chanNguoiDung(idNguoiChan, idNguoiBiChan);
        return ResponseEntity.ok(Map.of("message", "Chặn người dùng thành công."));
    }

    @DeleteMapping("/bo-chan/{idNguoiBiChan}")
    public ResponseEntity<Map<String, String>> boChanNguoiDung(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer idNguoiBiChan) {
        Integer idNguoiChan = getUserIdFromToken(authHeader);
        ketBanService.boChanNguoiDung(idNguoiChan, idNguoiBiChan);
        return ResponseEntity.ok(Map.of("message", "Bỏ chặn người dùng thành công."));
    }

    @GetMapping("/danh-sach/ban-be")
    public ResponseEntity<?> layDanhSachBanBe(@RequestHeader("Authorization") String authorization) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thiếu hoặc sai định dạng Authorization header");
            }
            String token = authorization.substring(7);
            NguoiDung nguoiDung = tokenUtil.layNguoiDungTuToken(token);
            Integer idNguoiDung = nguoiDung.getId();
            return ResponseEntity.ok(ketBanService.danhSachBanBe(idNguoiDung, Pageable.unpaged()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ hoặc đã hết hạn");
        }
    }

    @GetMapping("/danh-sach/loi-moi-nhan")
    public ResponseEntity<Page<LoiMoiKetBanDaNhanDTO>> danhSachLoiMoiNhan(
            @RequestHeader("Authorization") String authHeader, Pageable pageable) {
        Integer idNguoiDung = getUserIdFromToken(authHeader);
        return ResponseEntity.ok(ketBanService.danhSachLoiMoiKetBan(idNguoiDung, pageable));
    }

    @GetMapping("/danh-sach/loi-moi-gui")
    public ResponseEntity<Page<LoiMoiKetBanDaGuiDTO>> danhSachLoiMoiGui(
            @RequestHeader("Authorization") String authHeader, Pageable pageable) {
        Integer idNguoiDung = getUserIdFromToken(authHeader);
        return ResponseEntity.ok(ketBanService.danhSachLoiMoiDaGui(idNguoiDung, pageable));
    }

    @GetMapping("/danh-sach/chan")
    public ResponseEntity<Page<com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO>> danhSachChan(
            @RequestHeader("Authorization") String authHeader, Pageable pageable) {
        Integer idNguoiDung = getUserIdFromToken(authHeader);
        return ResponseEntity.ok(ketBanService.danhSachNguoiDungBiChan(idNguoiDung, pageable));
    }

    @GetMapping("/trang-thai/{idBanBe}")
    public ResponseEntity<Map<String, Object>> kiemTraTrangThai(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer idBanBe) {
        Integer idNguoiDung = getUserIdFromToken(authHeader);
        com.mangxahoi.mangxahoi_backend.enums.TrangThaiKetBan status = ketBanService.kiemTraTrangThaiKetBan(idNguoiDung, idBanBe);
        return ResponseEntity.ok(Map.of("status", status != null ? status.name() : "khong_quen"));
    }

    @GetMapping("/dem/ban-be")
    public ResponseEntity<Map<String, Long>> demSoBanBe(@RequestHeader("Authorization") String authHeader) {
        Integer idNguoiDung = getUserIdFromToken(authHeader);
        return ResponseEntity.ok(Map.of("count", ketBanService.demSoBanBe(idNguoiDung)));
    }

    @GetMapping("/dem/loi-moi-nhan")
    public ResponseEntity<Map<String, Long>> demSoLoiMoiNhan(@RequestHeader("Authorization") String authHeader) {
        Integer idNguoiDung = getUserIdFromToken(authHeader);
        return ResponseEntity.ok(Map.of("count", ketBanService.demSoLoiMoiKetBan(idNguoiDung)));
    }
} 
package com.mangxahoi.mangxahoi_backend.controller;

import com.mangxahoi.mangxahoi_backend.entity.BaoCao;
import com.mangxahoi.mangxahoi_backend.entity.BaiViet;
import com.mangxahoi.mangxahoi_backend.entity.BinhLuan;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.enums.LoaiBaoCao;
import com.mangxahoi.mangxahoi_backend.enums.TrangThaiBaoCao;
import com.mangxahoi.mangxahoi_backend.repository.BaoCaoRepository;
import com.mangxahoi.mangxahoi_backend.repository.BaiVietRepository;
import com.mangxahoi.mangxahoi_backend.repository.BinhLuanRepository;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import com.mangxahoi.mangxahoi_backend.dto.request.XuLyBaoCaoRequest;
import com.mangxahoi.mangxahoi_backend.util.TokenUtil;
import com.mangxahoi.mangxahoi_backend.service.BaoCaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/bao-cao")
@RequiredArgsConstructor
public class BaoCaoController {
    private final BaoCaoRepository baoCaoRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final BaiVietRepository baiVietRepository;
    private final BinhLuanRepository binhLuanRepository;
    private final TokenUtil tokenUtil;
    private final BaoCaoService baoCaoService;

    // Gửi báo cáo
    @PostMapping("/guibaocao")
    public ResponseEntity<?> guiBaoCao(@RequestBody BaoCao baoCao, @RequestHeader("Authorization") String authorization) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thiếu hoặc sai định dạng Authorization header");
            }
            String token = authorization.substring(7);
            NguoiDung nguoiDung = tokenUtil.layNguoiDungTuToken(token);
            return baoCaoService.guiBaoCao(baoCao, nguoiDung);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ hoặc đã hết hạn");
        }
    }

    // Lấy danh sách báo cáo (admin)
    @GetMapping("/danh-sach")
    public ResponseEntity<?> layDanhSachBaoCao(Pageable pageable, @RequestParam(required = false) String trangThai, @RequestHeader("Authorization") String authorization) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thiếu hoặc sai định dạng Authorization header");
            }
            String token = authorization.substring(7);
            NguoiDung nguoiDung = tokenUtil.layNguoiDungTuToken(token);
            if (!"quan_tri_vien".equalsIgnoreCase(nguoiDung.getVaiTro().name())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ admin mới được xem danh sách báo cáo");
            }
            Page<BaoCao> page;
            if (trangThai != null) {
                try {
                    TrangThaiBaoCao ttb = TrangThaiBaoCao.valueOf(trangThai);
                    page = baoCaoRepository.findByTrangThai(ttb, pageable);
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body("Trạng thái báo cáo không hợp lệ");
                }
            } else {
                page = baoCaoRepository.findAll(pageable);
            }
            return ResponseEntity.ok(page);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ hoặc đã hết hạn");
        }
    }

    // Lấy báo cáo của 1 user
    @GetMapping("/nguoi-dung/{idNguoiDung}")
    public ResponseEntity<?> layBaoCaoNguoiDung(@PathVariable Integer idNguoiDung, @RequestHeader("Authorization") String authorization) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thiếu hoặc sai định dạng Authorization header");
            }
            String token = authorization.substring(7);
            NguoiDung nguoiDung = tokenUtil.layNguoiDungTuToken(token);
            if (!nguoiDung.getId().equals(idNguoiDung) && !"quan_tri_vien".equalsIgnoreCase(nguoiDung.getVaiTro().name())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ được xem báo cáo của chính mình hoặc là admin");
            }
            Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findById(idNguoiDung);
            if (nguoiDungOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
            }
            return ResponseEntity.ok(baoCaoRepository.findByNguoiBaoCao(nguoiDungOpt.get()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ hoặc đã hết hạn");
        }
    }

    @PutMapping("/{id}/xu-ly")
    public ResponseEntity<?> xuLyBaoCao(
            @PathVariable Integer id,
            @RequestBody XuLyBaoCaoRequest request,
            @RequestHeader("Authorization") String authorization) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thiếu hoặc sai định dạng Authorization header");
            }
            String token = authorization.substring(7);
            NguoiDung nguoiDung = tokenUtil.layNguoiDungTuToken(token);
            if (!"quan_tri_vien".equalsIgnoreCase(nguoiDung.getVaiTro().name())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chỉ admin mới được xử lý báo cáo");
            }
            Optional<BaoCao> baoCaoOpt = baoCaoRepository.findById(id);
            if (baoCaoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Báo cáo không tồn tại");
            }
            BaoCao baoCao = baoCaoOpt.get();
            baoCao.setTrangThai(request.getTrangThai());
            baoCao.setGhiChuXuLy(request.getGhiChuXuLy());
            baoCao.setNgayXuLy(LocalDateTime.now());
            baoCaoRepository.save(baoCao);
            return ResponseEntity.ok("Đã cập nhật trạng thái báo cáo");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ hoặc đã hết hạn");
        }
    }
} 
package com.mangxahoi.mangxahoi_backend.controller;

import com.mangxahoi.mangxahoi_backend.dto.request.GuiThongBaoRequest;
import com.mangxahoi.mangxahoi_backend.entity.*;
import com.mangxahoi.mangxahoi_backend.enums.LoaiThongBao;
import com.mangxahoi.mangxahoi_backend.enums.MucDoUuTien;
import com.mangxahoi.mangxahoi_backend.repository.*;
import com.mangxahoi.mangxahoi_backend.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/thong-bao")
@RequiredArgsConstructor
public class ThongBaoController {
    private final ThongBaoRepository thongBaoRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final ThongBaoHeThongRepository thongBaoHeThongRepository;
    private final ThongBaoKetBanRepository thongBaoKetBanRepository;
    private final ThongBaoTinNhanRepository thongBaoTinNhanRepository;
    private final ThongBaoTuongTacRepository thongBaoTuongTacRepository;
    private final TokenUtil tokenUtil;

    @PostMapping("/gui")
    public ResponseEntity<?> guiThongBao(@RequestHeader("Authorization") String authorization,
                                         @RequestBody GuiThongBaoRequest request) {
        try {
            String token = authorization;
            if (authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
            }
            // Có thể kiểm tra quyền ở đây nếu cần
            Optional<NguoiDung> nguoiNhanOpt = nguoiDungRepository.findById(request.getIdNguoiNhan());
            if (nguoiNhanOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người nhận không tồn tại");
            }
            NguoiDung nguoiNhan = nguoiNhanOpt.get();
            // Validate loại thông báo
            LoaiThongBao loaiThongBao;
            try {
                loaiThongBao = LoaiThongBao.valueOf(request.getLoai());
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Loại thông báo không hợp lệ");
            }
            // Validate mức độ ưu tiên
            String mucDoUuTien = request.getMucDoUuTien() != null ? request.getMucDoUuTien() : "trung_binh";
            if (!Arrays.asList("thap", "trung_binh", "cao").contains(mucDoUuTien)) {
                mucDoUuTien = "trung_binh";
            }
            // Tạo bản ghi ThongBao
            ThongBao thongBao = ThongBao.builder()
                    .nguoiNhan(nguoiNhan)
                    .loai(loaiThongBao.name())
                    .tieuDe(request.getTieuDe())
                    .noiDung(request.getNoiDung())
                    .mucDoUuTien(mucDoUuTien)
                    .build();
            thongBao = thongBaoRepository.save(thongBao);
            // Tạo bản ghi chi tiết nếu có
            switch (loaiThongBao) {
                case he_thong -> {
                    ThongBaoHeThong tbht = ThongBaoHeThong.builder()
                            .thongBao(thongBao)
                            .loaiHeThong(null) // Có thể truyền thêm nếu muốn
                            .urlHanhDong(null)
                            .build();
                    thongBaoHeThongRepository.save(tbht);
                }
                case moi_ket_ban, chap_nhan_ban -> {
                    if (request.getIdKetBan() != null) {
                        ThongBaoKetBan tbkb = ThongBaoKetBan.builder()
                                .thongBao(thongBao)
                                .nguoiGui(null) // Có thể truyền thêm nếu muốn
                                .ketBan(null) // Có thể truyền thêm nếu muốn
                                .loaiKetBan(null)
                                .build();
                        thongBaoKetBanRepository.save(tbkb);
                    }
                }
                case nhan_tin -> {
                    if (request.getIdTinNhan() != null) {
                        ThongBaoTinNhan tbtn = ThongBaoTinNhan.builder()
                                .thongBao(thongBao)
                                .cuocTroChuyen(null)
                                .tinNhan(null)
                                .build();
                        thongBaoTinNhanRepository.save(tbtn);
                    }
                }
                case tuong_tac -> {
                    ThongBaoTuongTac tbt = ThongBaoTuongTac.builder()
                            .thongBao(thongBao)
                            .nguoiGui(null)
                            .loaiTuongTac(null)
                            .baiViet(null)
                            .binhLuan(null)
                            .noiDungTuongTac(null)
                            .build();
                    thongBaoTuongTacRepository.save(tbt);
                }
            }
            return ResponseEntity.ok("Đã gửi thông báo thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/nguoi-dung/{idNguoiDung}")
    public ResponseEntity<?> layThongBaoNguoiDung(@PathVariable Integer idNguoiDung, @RequestHeader("Authorization") String authorization) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thiếu hoặc sai định dạng Authorization header");
            }
            String token = authorization.substring(7);
            NguoiDung nguoiDung = tokenUtil.layNguoiDungTuToken(token);
            // Có thể kiểm tra quyền ở đây nếu cần
            Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findById(idNguoiDung);
            if (nguoiDungOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
            }
            List<ThongBao> thongBaos = thongBaoRepository.findByNguoiNhan(nguoiDungOpt.get());
            // Chuyển đổi sang DTO
            List<com.mangxahoi.mangxahoi_backend.dto.ThongBaoDTO> thongBaoDTOs = thongBaos.stream().map(tb -> {
                com.mangxahoi.mangxahoi_backend.dto.ThongBaoDTO dto = new com.mangxahoi.mangxahoi_backend.dto.ThongBaoDTO();
                dto.setId(tb.getId());
                dto.setLoai(tb.getLoai());
                dto.setTieuDe(tb.getTieuDe());
                dto.setNoiDung(tb.getNoiDung());
                dto.setDaDoc(tb.getDaDoc());
                dto.setMucDoUuTien(tb.getMucDoUuTien());
                dto.setNgayTao(tb.getNgayTao());
                if (tb.getNguoiNhan() != null) {
                    dto.setNguoiNhanId(tb.getNguoiNhan().getId());
                    dto.setNguoiNhanTen(tb.getNguoiNhan().getHoTen());
                }
                return dto;
            }).toList();
            return ResponseEntity.ok(thongBaoDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ hoặc đã hết hạn");
        }
    }
}

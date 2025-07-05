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
    private final BaiVietRepository baiVietRepository;
    private final BinhLuanRepository binhLuanRepository;
    private final KetBanRepository ketBanRepository;
    private final TinNhanRepository tinNhanRepository;
    private final CuocTroChuyenRepository cuocTroChuyenRepository;
    private final TokenUtil tokenUtil;

    @PostMapping("/gui")
    public ResponseEntity<?> guiThongBao(@RequestHeader("Authorization") String authorization,
                                         @RequestBody GuiThongBaoRequest request) {
        try {
            String token = authorization;
            if (authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
            }
            
            // Lấy thông tin người gửi từ token
            NguoiDung nguoiGui = tokenUtil.layNguoiDungTuToken(token);
            
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
            
            // Tạo bản ghi chi tiết theo loại thông báo
            switch (loaiThongBao) {
                case he_thong -> {
                    ThongBaoHeThong tbht = ThongBaoHeThong.builder()
                            .thongBao(thongBao)
                            .loaiHeThong(request.getLoaiHeThong())
                            .urlHanhDong(request.getUrlHanhDong())
                            .build();
                    thongBaoHeThongRepository.save(tbht);
                }
                case moi_ket_ban, chap_nhan_ban -> {
                    if (request.getIdKetBan() != null) {
                        Optional<KetBan> ketBanOpt = ketBanRepository.findById(request.getIdKetBan());
                        if (ketBanOpt.isPresent()) {
                            ThongBaoKetBan tbkb = ThongBaoKetBan.builder()
                                    .thongBao(thongBao)
                                    .nguoiGui(nguoiGui)
                                    .ketBan(ketBanOpt.get())
                                    .loaiKetBan(request.getLoaiKetBan())
                                    .build();
                            thongBaoKetBanRepository.save(tbkb);
                        }
                    }
                }
                case nhan_tin -> {
                    if (request.getIdTinNhan() != null) {
                        Optional<TinNhan> tinNhanOpt = tinNhanRepository.findById(request.getIdTinNhan());
                        Optional<CuocTroChuyen> cuocTroChuyenOpt = cuocTroChuyenRepository.findById(request.getIdCuocTroChuyen());
                        if (tinNhanOpt.isPresent() && cuocTroChuyenOpt.isPresent()) {
                            ThongBaoTinNhan tbtn = ThongBaoTinNhan.builder()
                                    .thongBao(thongBao)
                                    .cuocTroChuyen(cuocTroChuyenOpt.get())
                                    .tinNhan(tinNhanOpt.get())
                                    .build();
                            thongBaoTinNhanRepository.save(tbtn);
                        }
                    }
                }
                case tuong_tac -> {
                    BaiViet baiViet = null;
                    BinhLuan binhLuan = null;
                    
                    // Lấy thông tin bài viết nếu có
                    if (request.getIdBaiViet() != null) {
                        Optional<BaiViet> baiVietOpt = baiVietRepository.findById(request.getIdBaiViet());
                        baiViet = baiVietOpt.orElse(null);
                    }
                    
                    // Lấy thông tin bình luận nếu có
                    if (request.getIdBinhLuan() != null) {
                        Optional<BinhLuan> binhLuanOpt = binhLuanRepository.findById(request.getIdBinhLuan());
                        binhLuan = binhLuanOpt.orElse(null);
                    }
                    
                    ThongBaoTuongTac tbt = ThongBaoTuongTac.builder()
                            .thongBao(thongBao)
                            .nguoiGui(nguoiGui)
                            .loaiTuongTac(request.getLoaiTuongTac())
                            .baiViet(baiViet)
                            .binhLuan(binhLuan)
                            .noiDungTuongTac(request.getNoiDungTuongTac())
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
                // Lấy thông tin chi tiết theo loại
                switch (tb.getLoai()) {
                    case "tuong_tac" -> {
                        if (tb.getThongBaoTuongTacs() != null && !tb.getThongBaoTuongTacs().isEmpty()) {
                            ThongBaoTuongTac tbt = tb.getThongBaoTuongTacs().get(0);
                            if (tbt.getBaiViet() != null) dto.setIdBaiViet(tbt.getBaiViet().getId());
                            if (tbt.getBinhLuan() != null) dto.setIdBinhLuan(tbt.getBinhLuan().getId());
                            dto.setLoaiTuongTac(tbt.getLoaiTuongTac());
                            dto.setNoiDungTuongTac(tbt.getNoiDungTuongTac());
                            if (tbt.getNguoiGui() != null) {
                                dto.setIdNguoiGui(tbt.getNguoiGui().getId());
                                dto.setTenNguoiGui(tbt.getNguoiGui().getHoTen());
                            }
                        }
                    }
                    case "moi_ket_ban", "chap_nhan_ban" -> {
                        if (tb.getThongBaoKetBans() != null && !tb.getThongBaoKetBans().isEmpty()) {
                            ThongBaoKetBan tbkb = tb.getThongBaoKetBans().get(0);
                            if (tbkb.getNguoiGui() != null) {
                                dto.setIdNguoiGui(tbkb.getNguoiGui().getId());
                                dto.setTenNguoiGui(tbkb.getNguoiGui().getHoTen());
                            }
                            if (tbkb.getKetBan() != null) {
                                dto.setIdKetBan(tbkb.getKetBan().getId());
                            }
                            dto.setLoaiKetBan(tbkb.getLoaiKetBan());
                        }
                    }
                    case "nhan_tin" -> {
                        if (tb.getThongBaoTinNhans() != null && !tb.getThongBaoTinNhans().isEmpty()) {
                            ThongBaoTinNhan tbtn = tb.getThongBaoTinNhans().get(0);
                            if (tbtn.getTinNhan() != null) dto.setIdTinNhan(tbtn.getTinNhan().getId());
                            if (tbtn.getCuocTroChuyen() != null) dto.setIdCuocTroChuyen(tbtn.getCuocTroChuyen().getId());
                        }
                    }
                    case "he_thong" -> {
                        if (tb.getThongBaoHeThongs() != null && !tb.getThongBaoHeThongs().isEmpty()) {
                            ThongBaoHeThong tbht = tb.getThongBaoHeThongs().get(0);
                            dto.setLoaiHeThong(tbht.getLoaiHeThong());
                            dto.setUrlHanhDong(tbht.getUrlHanhDong());
                        }
                    }
                }
                return dto;
            }).toList();
            return ResponseEntity.ok(thongBaoDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ hoặc đã hết hạn");
        }
    }

    /**
     * Đánh dấu thông báo đã đọc
     */
    @PutMapping("/da-doc/{idThongBao}")
    public ResponseEntity<?> danhDauDaDoc(@PathVariable Integer idThongBao, @RequestHeader("Authorization") String authorization) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thiếu hoặc sai định dạng Authorization header");
            }
            String token = authorization.substring(7);
            NguoiDung nguoiDung = tokenUtil.layNguoiDungTuToken(token);
            
            Optional<ThongBao> thongBaoOpt = thongBaoRepository.findById(idThongBao);
            if (thongBaoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Thông báo không tồn tại");
            }
            
            ThongBao thongBao = thongBaoOpt.get();
            // Kiểm tra quyền - chỉ người nhận mới có thể đánh dấu đã đọc
            if (!thongBao.getNguoiNhan().getId().equals(nguoiDung.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền thực hiện hành động này");
            }
            
            thongBao.setDaDoc(true);
            thongBaoRepository.save(thongBao);
            
            return ResponseEntity.ok("Đã đánh dấu thông báo đã đọc");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Đánh dấu tất cả thông báo đã đọc
     */
    @PutMapping("/da-doc-tat-ca/{idNguoiDung}")
    public ResponseEntity<?> danhDauTatCaDaDoc(@PathVariable Integer idNguoiDung, @RequestHeader("Authorization") String authorization) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thiếu hoặc sai định dạng Authorization header");
            }
            String token = authorization.substring(7);
            NguoiDung nguoiDung = tokenUtil.layNguoiDungTuToken(token);
            
            // Kiểm tra quyền
            if (!nguoiDung.getId().equals(idNguoiDung)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền thực hiện hành động này");
            }
            
            thongBaoRepository.markAllAsRead(nguoiDung);
            
            return ResponseEntity.ok("Đã đánh dấu tất cả thông báo đã đọc");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Đếm số thông báo chưa đọc
     */
    @GetMapping("/dem-chua-doc/{idNguoiDung}")
    public ResponseEntity<?> demThongBaoChuaDoc(@PathVariable Integer idNguoiDung, @RequestHeader("Authorization") String authorization) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Thiếu hoặc sai định dạng Authorization header");
            }
            String token = authorization.substring(7);
            NguoiDung nguoiDung = tokenUtil.layNguoiDungTuToken(token);
            
            // Kiểm tra quyền
            if (!nguoiDung.getId().equals(idNguoiDung)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền thực hiện hành động này");
            }
            
            long soThongBaoChuaDoc = thongBaoRepository.countUnreadNotifications(nguoiDung);
            
            return ResponseEntity.ok(Map.of("soThongBaoChuaDoc", soThongBaoChuaDoc));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }
}

package com.mangxahoi.mangxahoi_backend.service.impl;

import com.mangxahoi.mangxahoi_backend.entity.LichSuGoiY;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.repository.KetBanRepository;
import com.mangxahoi.mangxahoi_backend.repository.LichSuGoiYRepository;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import com.mangxahoi.mangxahoi_backend.repository.ChiTietTuongTacRepository;
import com.mangxahoi.mangxahoi_backend.service.GoiYKetBanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.mangxahoi.mangxahoi_backend.entity.KetBan;

@Service
@RequiredArgsConstructor
public class GoiYKetBanServiceImpl implements GoiYKetBanService {
    private final NguoiDungRepository nguoiDungRepository;
    private final KetBanRepository ketBanRepository;
    private final LichSuGoiYRepository lichSuGoiYRepository;
    private final ChiTietTuongTacRepository chiTietTuongTacRepository;

    @Override
    @Transactional
    public void taoGoiYKetBan(Integer idNguoiDung) {
        NguoiDung nguoiDuocGoiY = nguoiDungRepository.findById(idNguoiDung)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<NguoiDung> allUsers = nguoiDungRepository.findAll();
        for (NguoiDung nguoiTrongGoiY : allUsers) {
            if (nguoiTrongGoiY.getId().equals(idNguoiDung)) continue;

            // BỎ QUA ADMIN/QUẢN TRỊ VIÊN
            if (nguoiTrongGoiY.getVaiTro() != null && nguoiTrongGoiY.getVaiTro().name().equalsIgnoreCase("quan_tri_vien")) continue;

            // BỎ QUA TÀI KHOẢN ĐỂ RIÊNG TƯ
            if (nguoiTrongGoiY.getMucRiengTu() != null && nguoiTrongGoiY.getMucRiengTu().name().equalsIgnoreCase("rieng_tu")) continue;

            // Đã là bạn
            boolean daLaBan = ketBanRepository.areFriends(nguoiDuocGoiY, nguoiTrongGoiY).isPresent();
            if (daLaBan) continue;

            // Đã gửi lời mời, đã bị chặn, đã bỏ qua (ở lịch sử gợi ý)
            Optional<LichSuGoiY> lichSuOpt = lichSuGoiYRepository.findByNguoiDuocGoiYAndNguoiTrongGoiY(nguoiDuocGoiY, nguoiTrongGoiY);
            boolean daChan = false, daBoQua = false, daGuiLoiMoi = false;
            if (lichSuOpt.isPresent()) {
                LichSuGoiY lichSu = lichSuOpt.get();
                daChan = Boolean.TRUE.equals(lichSu.getDaChan());
                daBoQua = Boolean.TRUE.equals(lichSu.getDaBoQua());
                daGuiLoiMoi = Boolean.TRUE.equals(lichSu.getDaGuiLoiMoi());
            }
            if (daChan || daBoQua || daGuiLoiMoi) continue;

            // BỎ QUA nếu có mối quan hệ chặn giữa 2 người (bất kỳ chiều nào)
            Optional<KetBan> block = ketBanRepository.findRelationship(nguoiDuocGoiY, nguoiTrongGoiY);
            if (block.isPresent() && block.get().getTrangThai() == com.mangxahoi.mangxahoi_backend.enums.TrangThaiKetBan.bi_chan) {
                continue;
            }

            // --- TÍNH ĐIỂM GỢI Ý ---
            // 1. Bạn chung
            // Lấy danh sách bạn bè của người được gợi ý
            List<KetBan> ketBansNguoiDuocGoiY = ketBanRepository.findAllFriends(nguoiDuocGoiY);
            List<NguoiDung> banBeNguoiDuocGoiY = ketBansNguoiDuocGoiY.stream()
                    .map(kb -> kb.getNguoiGui().equals(nguoiDuocGoiY) ? kb.getNguoiNhan() : kb.getNguoiGui())
                    .collect(Collectors.toList());

            // Lấy danh sách bạn bè của người trong gợi ý
            List<KetBan> ketBansNguoiTrongGoiY = ketBanRepository.findAllFriends(nguoiTrongGoiY);
            List<NguoiDung> banBeNguoiTrongGoiY = ketBansNguoiTrongGoiY.stream()
                    .map(kb -> kb.getNguoiGui().equals(nguoiTrongGoiY) ? kb.getNguoiNhan() : kb.getNguoiGui())
                    .collect(Collectors.toList());
            int soBanChung = (int) banBeNguoiDuocGoiY.stream()
                    .filter(banBeNguoiTrongGoiY::contains)
                    .count();
            int diemBanChung = soBanChung * 10;

            // 2. Tương tác (like, bình luận, v.v.)
            int diemTuongTac = 0;
            Integer tongDiemTuongTac = chiTietTuongTacRepository.getTotalInteractionPoints(nguoiDuocGoiY, nguoiTrongGoiY);
            if (tongDiemTuongTac != null) diemTuongTac += tongDiemTuongTac;
            Integer tongDiemTuongTac2 = chiTietTuongTacRepository.getTotalInteractionPoints(nguoiTrongGoiY, nguoiDuocGoiY);
            if (tongDiemTuongTac2 != null) diemTuongTac += tongDiemTuongTac2;

            // Tổng điểm
            int diemGoiY = diemBanChung + diemTuongTac;

            // --- TẠO GỢI Ý ---
           if (diemGoiY > 0) {
                if (lichSuOpt.isEmpty()) {
                    LichSuGoiY goiY = LichSuGoiY.builder()
                            .nguoiDuocGoiY(nguoiDuocGoiY)
                            .nguoiTrongGoiY(nguoiTrongGoiY)
                            .diemGoiY(diemGoiY)
                            .nguonGoiY("java")
                            .build();
                    lichSuGoiYRepository.save(goiY);
                } else {
                    // Nếu đã có, cập nhật điểm gợi ý mới
                    LichSuGoiY lichSu = lichSuOpt.get();
                    lichSu.setDiemGoiY(diemGoiY);
                    lichSuGoiYRepository.save(lichSu);
                }
            }
        }
    }
} 
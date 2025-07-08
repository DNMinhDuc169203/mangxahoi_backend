package com.mangxahoi.mangxahoi_backend.service;

import com.mangxahoi.mangxahoi_backend.dto.request.GuiThongBaoRequest;
import com.mangxahoi.mangxahoi_backend.entity.*;
import com.mangxahoi.mangxahoi_backend.enums.LoaiThongBao;
import com.mangxahoi.mangxahoi_backend.enums.LoaiTuongTacThongBao;
import com.mangxahoi.mangxahoi_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ThongBaoService {
    private final ThongBaoRepository thongBaoRepository;
    private final ThongBaoTuongTacRepository thongBaoTuongTacRepository;
    private final ThongBaoKetBanRepository thongBaoKetBanRepository;
    private final ThongBaoTinNhanRepository thongBaoTinNhanRepository;
    private final ThongBaoHeThongRepository thongBaoHeThongRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final BaiVietRepository baiVietRepository;
    private final BinhLuanRepository binhLuanRepository;
    private final KetBanRepository ketBanRepository;
    private final TinNhanRepository tinNhanRepository;
    private final CuocTroChuyenRepository cuocTroChuyenRepository;

    /**
     * Gửi thông báo khi có người thích bài viết
     */
    @Transactional
    public void guiThongBaoThichBaiViet(Integer idNguoiThich, Integer idBaiViet) {
        try {
            BaiViet baiViet = baiVietRepository.findById(idBaiViet).orElse(null);
            if (baiViet == null) return;

            NguoiDung nguoiThich = nguoiDungRepository.findById(idNguoiThich).orElse(null);
            if (nguoiThich == null) return;

            // Không gửi thông báo cho chính mình
            if (baiViet.getNguoiDung().getId().equals(idNguoiThich)) return;

            ThongBao thongBao = ThongBao.builder()
                    .nguoiNhan(baiViet.getNguoiDung())
                    .loai(LoaiThongBao.tuong_tac.name())
                    .tieuDe("Bài viết của bạn vừa được thích!")
                    .noiDung("Người dùng " + nguoiThich.getHoTen() + " vừa thích bài viết của bạn.")
                    .mucDoUuTien("trung_binh")
                    .build();
            thongBao = thongBaoRepository.save(thongBao);

            ThongBaoTuongTac tbt = ThongBaoTuongTac.builder()
                    .thongBao(thongBao)
                    .nguoiGui(nguoiThich)
                    .loaiTuongTac(LoaiTuongTacThongBao.thich_bai_viet.name())
                    .baiViet(baiViet)
                    .binhLuan(null)
                    .noiDungTuongTac("Đã thích bài viết của bạn")
                    .build();
            thongBaoTuongTacRepository.save(tbt);
        } catch (Exception e) {
            // Log lỗi nhưng không throw để không ảnh hưởng đến luồng chính
            System.err.println("Lỗi gửi thông báo thích bài viết: " + e.getMessage());
        }
    }

    /**
     * Gửi thông báo khi có người thích bình luận
     */
    @Transactional
    public void guiThongBaoThichBinhLuan(Integer idNguoiThich, Integer idBinhLuan) {
        try {
            BinhLuan binhLuan = binhLuanRepository.findById(idBinhLuan).orElse(null);
            if (binhLuan == null) return;

            NguoiDung nguoiThich = nguoiDungRepository.findById(idNguoiThich).orElse(null);
            if (nguoiThich == null) return;

            // Không gửi thông báo cho chính mình
            if (binhLuan.getNguoiDung().getId().equals(idNguoiThich)) return;

            ThongBao thongBao = ThongBao.builder()
                    .nguoiNhan(binhLuan.getNguoiDung())
                    .loai(LoaiThongBao.tuong_tac.name())
                    .tieuDe("Bình luận của bạn vừa được thích!")
                    .noiDung("Người dùng " + nguoiThich.getHoTen() + " vừa thích bình luận của bạn.")
                    .mucDoUuTien("trung_binh")
                    .build();
            thongBao = thongBaoRepository.save(thongBao);

            ThongBaoTuongTac tbt = ThongBaoTuongTac.builder()
                    .thongBao(thongBao)
                    .nguoiGui(nguoiThich)
                    .loaiTuongTac(LoaiTuongTacThongBao.thich_binh_luan.name())
                    .baiViet(binhLuan.getBaiViet())
                    .binhLuan(binhLuan)
                    .noiDungTuongTac("Đã thích bình luận của bạn")
                    .build();
            thongBaoTuongTacRepository.save(tbt);
        } catch (Exception e) {
            System.err.println("Lỗi gửi thông báo thích bình luận: " + e.getMessage());
        }
    }

    /**
     * Gửi thông báo khi có người bình luận bài viết
     */
    @Transactional
    public void guiThongBaoBinhLuan(Integer idNguoiBinhLuan, Integer idBaiViet) {
        try {
            BaiViet baiViet = baiVietRepository.findById(idBaiViet).orElse(null);
            if (baiViet == null) return;

            NguoiDung nguoiBinhLuan = nguoiDungRepository.findById(idNguoiBinhLuan).orElse(null);
            if (nguoiBinhLuan == null) return;

            // Không gửi thông báo cho chính mình
            if (baiViet.getNguoiDung().getId().equals(idNguoiBinhLuan)) return;

            ThongBao thongBao = ThongBao.builder()
                    .nguoiNhan(baiViet.getNguoiDung())
                    .loai(LoaiThongBao.tuong_tac.name())
                    .tieuDe("Bài viết của bạn vừa có bình luận mới!")
                    .noiDung("Người dùng " + nguoiBinhLuan.getHoTen() + " vừa bình luận vào bài viết của bạn.")
                    .mucDoUuTien("trung_binh")
                    .build();
            thongBao = thongBaoRepository.save(thongBao);

            ThongBaoTuongTac tbt = ThongBaoTuongTac.builder()
                    .thongBao(thongBao)
                    .nguoiGui(nguoiBinhLuan)
                    .loaiTuongTac(LoaiTuongTacThongBao.binh_luan.name())
                    .baiViet(baiViet)
                    .binhLuan(null)
                    .noiDungTuongTac("Đã bình luận vào bài viết của bạn")
                    .build();
            thongBaoTuongTacRepository.save(tbt);
        } catch (Exception e) {
            System.err.println("Lỗi gửi thông báo bình luận: " + e.getMessage());
        }
    }

    /**
     * Gửi thông báo khi có người trả lời bình luận
     */
    @Transactional
    public void guiThongBaoTraLoiBinhLuan(Integer idNguoiTraLoi, Integer idBinhLuanCha) {
        try {
            BinhLuan binhLuanCha = binhLuanRepository.findById(idBinhLuanCha).orElse(null);
            if (binhLuanCha == null) return;

            NguoiDung nguoiTraLoi = nguoiDungRepository.findById(idNguoiTraLoi).orElse(null);
            if (nguoiTraLoi == null) return;

            // Không gửi thông báo cho chính mình
            if (binhLuanCha.getNguoiDung().getId().equals(idNguoiTraLoi)) return;

            ThongBao thongBao = ThongBao.builder()
                    .nguoiNhan(binhLuanCha.getNguoiDung())
                    .loai(LoaiThongBao.tuong_tac.name())
                    .tieuDe("Bình luận của bạn vừa có trả lời mới!")
                    .noiDung("Người dùng " + nguoiTraLoi.getHoTen() + " vừa trả lời bình luận của bạn.")
                    .mucDoUuTien("trung_binh")
                    .build();
            thongBao = thongBaoRepository.save(thongBao);

            ThongBaoTuongTac tbt = ThongBaoTuongTac.builder()
                    .thongBao(thongBao)
                    .nguoiGui(nguoiTraLoi)
                    .loaiTuongTac(LoaiTuongTacThongBao.tra_loi_binh_luan.name())
                    .baiViet(binhLuanCha.getBaiViet())
                    .binhLuan(binhLuanCha)
                    .noiDungTuongTac("Đã trả lời bình luận của bạn")
                    .build();
            thongBaoTuongTacRepository.save(tbt);
        } catch (Exception e) {
            System.err.println("Lỗi gửi thông báo trả lời bình luận: " + e.getMessage());
        }
    }

    /**
     * Gửi thông báo khi có lời mời kết bạn
     */
    @Transactional
    public void guiThongBaoLoiMoiKetBan(Integer idNguoiGui, Integer idKetBan) {
        try {
            KetBan ketBan = ketBanRepository.findById(idKetBan).orElse(null);
            if (ketBan == null) return;

            NguoiDung nguoiGui = nguoiDungRepository.findById(idNguoiGui).orElse(null);
            if (nguoiGui == null) return;

            ThongBao thongBao = ThongBao.builder()
                    .nguoiNhan(ketBan.getNguoiNhan())
                    .loai(LoaiThongBao.moi_ket_ban.name())
                    .tieuDe("Bạn có lời mời kết bạn mới!")
                    .noiDung("Người dùng " + nguoiGui.getHoTen() + " vừa gửi lời mời kết bạn cho bạn.")
                    .mucDoUuTien("cao")
                    .build();
            thongBao = thongBaoRepository.save(thongBao);

            ThongBaoKetBan tbkb = ThongBaoKetBan.builder()
                    .thongBao(thongBao)
                    .nguoiGui(nguoiGui)
                    .ketBan(ketBan)
                    .loaiKetBan("moi_ket_ban")
                    .build();
            thongBaoKetBanRepository.save(tbkb);
        } catch (Exception e) {
            System.err.println("Lỗi gửi thông báo lời mời kết bạn: " + e.getMessage());
        }
    }

    /**
     * Gửi thông báo khi có tin nhắn mới
     */
    @Transactional
    public void guiThongBaoTinNhan(Integer idNguoiGui, Integer idTinNhan, Integer idCuocTroChuyen) {
        try {
            TinNhan tinNhan = tinNhanRepository.findById(idTinNhan).orElse(null);
            CuocTroChuyen cuocTroChuyen = cuocTroChuyenRepository.findById(idCuocTroChuyen).orElse(null);
            if (tinNhan == null || cuocTroChuyen == null) return;

            NguoiDung nguoiGui = nguoiDungRepository.findById(idNguoiGui).orElse(null);
            if (nguoiGui == null) return;

            // Tìm người nhận (người khác trong cuộc trò chuyện)
            NguoiDung nguoiNhan = null;
            if (cuocTroChuyen.getLoai().equals("ca_nhan")) {
                if (cuocTroChuyen.getNguoiTao().getId().equals(idNguoiGui)) {
                    nguoiNhan = cuocTroChuyen.getNguoiThamGia();
                } else {
                    nguoiNhan = cuocTroChuyen.getNguoiTao();
                }
            }
            // TODO: Xử lý cho nhóm chat

            if (nguoiNhan == null) return;

            ThongBao thongBao = ThongBao.builder()
                    .nguoiNhan(nguoiNhan)
                    .loai(LoaiThongBao.nhan_tin.name())
                    .tieuDe("Bạn có tin nhắn mới!")
                    .noiDung("Người dùng " + nguoiGui.getHoTen() + " vừa gửi tin nhắn cho bạn.")
                    .mucDoUuTien("cao")
                    .build();
            thongBao = thongBaoRepository.save(thongBao);

            ThongBaoTinNhan tbtn = ThongBaoTinNhan.builder()
                    .thongBao(thongBao)
                    .cuocTroChuyen(cuocTroChuyen)
                    .tinNhan(tinNhan)
                    .build();
            thongBaoTinNhanRepository.save(tbtn);
        } catch (Exception e) {
            System.err.println("Lỗi gửi thông báo tin nhắn: " + e.getMessage());
        }
    }

    /**
     * Xóa notification friend request khi đã chấp nhận kết bạn
     */
    @Transactional
    public void xoaThongBaoKetBanSauKhiChapNhan(Integer idKetBan) {
        KetBan ketBan = ketBanRepository.findById(idKetBan).orElse(null);
        if (ketBan == null) return;
        // Lấy tất cả ThongBaoKetBan liên quan đến KetBan này
        java.util.List<ThongBaoKetBan> thongBaoKetBans = thongBaoKetBanRepository.findByKetBan(ketBan);
        for (ThongBaoKetBan tbkb : thongBaoKetBans) {
            ThongBao thongBao = tbkb.getThongBao();
            thongBaoKetBanRepository.delete(tbkb); // Xóa chi tiết
            thongBaoRepository.delete(thongBao);   // Xóa bản ghi chính
        }
    }

    @Transactional
    public void guiThongBaoHeThong(Integer idNguoiNhan, String tieuDe, String noiDung) {
        NguoiDung nguoiNhan = nguoiDungRepository.findById(idNguoiNhan).orElse(null);
        if (nguoiNhan == null) return;

        ThongBao thongBao = ThongBao.builder()
                .nguoiNhan(nguoiNhan)
                .loai("he_thong")
                .tieuDe(tieuDe)
                .noiDung(noiDung)
                .mucDoUuTien("trung_binh")
                .build();
        thongBaoRepository.save(thongBao);
    }
} 
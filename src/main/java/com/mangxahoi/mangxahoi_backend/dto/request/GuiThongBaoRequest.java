package com.mangxahoi.mangxahoi_backend.dto.request;

import lombok.Data;

@Data
public class GuiThongBaoRequest {
    private Integer idNguoiNhan;
    private String loai; // tuong_tac, moi_ket_ban, nhan_tin, he_thong
    private String tieuDe;
    private String noiDung;
    // Các trường chi tiết cho từng loại (tùy chọn)
    private Integer idBaiViet; // cho tuong_tac
    private Integer idBinhLuan; // cho tuong_tac
    private Integer idKetBan; // cho ket_ban
    private Integer idTinNhan; // cho tin_nhan
    private Integer idCuocTroChuyen; // cho tin_nhan
    private String loaiTuongTac; // cho tuong_tac: thich_bai_viet, thich_binh_luan, binh_luan, tra_loi_binh_luan
    private String loaiKetBan; // cho ket_ban: moi_ket_ban, chap_nhan_ban
    private String loaiHeThong; // cho he_thong
    private String urlHanhDong; // cho he_thong
    private String noiDungTuongTac; // cho tuong_tac
    private String mucDoUuTien; // thap, trung_binh, cao
} 
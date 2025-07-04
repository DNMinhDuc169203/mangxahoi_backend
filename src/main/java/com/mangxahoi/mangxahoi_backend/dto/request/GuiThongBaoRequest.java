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
    private String mucDoUuTien; // thap, trung_binh, cao
} 
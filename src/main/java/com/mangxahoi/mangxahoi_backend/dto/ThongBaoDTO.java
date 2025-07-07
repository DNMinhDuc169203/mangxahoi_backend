package com.mangxahoi.mangxahoi_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ThongBaoDTO {
    private Integer id;
    private String anhDaiDienNguoiGui;
    private String loai;
    private String tieuDe;
    private String noiDung;
    private Boolean daDoc;
    private String mucDoUuTien;
    private LocalDateTime ngayTao;
    private String noiDungBaiViet;
    private Integer nguoiNhanId;
    private String nguoiNhanTen;
    private Boolean daChapNhan;
    private Integer idBaiViet;      // tuong_tac
    private Integer idBinhLuan;     // tuong_tac
    private String loaiTuongTac;    // tuong_tac
    private String noiDungTuongTac; // tuong_tac
    private Integer idNguoiGui;     // tuong_tac, ket_ban
    private String tenNguoiGui;     // tuong_tac, ket_ban
    private Integer idKetBan;       // ket_ban
    private String loaiKetBan;      // ket_ban
    private Integer idTinNhan;      // tin_nhan
    private Integer idCuocTroChuyen;// tin_nhan
    private String loaiHeThong;     // he_thong
    private String urlHanhDong;     // he_thong
} 
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
    private Integer idBaiViet;     
    private Integer idBinhLuan;     
    private String loaiTuongTac;    
    private String noiDungTuongTac; 
    private Integer idNguoiGui;    
    private String tenNguoiGui;     
    private Integer idKetBan;       
    private String loaiKetBan;    
    private Integer idTinNhan;      
    private Integer idCuocTroChuyen;
    private String loaiHeThong;    
    private String urlHanhDong;     
} 
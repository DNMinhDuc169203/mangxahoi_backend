package com.mangxahoi.mangxahoi_backend.admin.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import com.mangxahoi.mangxahoi_backend.enums.LoaiViPham;

@Data
public class LichSuViPhamDTO {
    private Integer id;
    private Integer userId;
    private String tenNguoiDung;
    private String noiDungViPham;
    private LoaiViPham loaiViPham;
    private LocalDateTime thoiGianViPham;
    private String hinhPhat;
    private String trangThaiXuLy;
    private Integer adminXuLyId;
    private String tenAdminXuLy;
    private String ghiChu;
    private Integer baoCaoId;

    public LoaiViPham getLoaiViPham() { return loaiViPham; }
    public void setLoaiViPham(LoaiViPham loaiViPham) { this.loaiViPham = loaiViPham; }
} 
package com.mangxahoi.mangxahoi_backend.admin.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LichSuViPhamDTO {
    private Integer id;
    private Integer userId;
    private String tenNguoiDung;
    private String noiDungViPham;
    private String loaiViPham;
    private LocalDateTime thoiGianViPham;
    private String hinhPhat;
    private String trangThaiXuLy;
    private Integer adminXuLyId;
    private String tenAdminXuLy;
    private String ghiChu;
    private Integer baoCaoId;
} 
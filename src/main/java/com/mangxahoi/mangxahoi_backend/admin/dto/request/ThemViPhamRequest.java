package com.mangxahoi.mangxahoi_backend.admin.dto.request;

import com.mangxahoi.mangxahoi_backend.enums.LoaiViPham;
import lombok.Data;

@Data
public class ThemViPhamRequest {
    private Integer userId;
    private String noiDungViPham;
    private LoaiViPham loaiViPham;
    private Integer baoCaoId; // nullable
    private String ghiChu; // nullable

    public LoaiViPham getLoaiViPham() { return loaiViPham; }
    public void setLoaiViPham(LoaiViPham loaiViPham) { this.loaiViPham = loaiViPham; }
} 
package com.mangxahoi.mangxahoi_backend.admin.dto.request;

import lombok.Data;

@Data
public class ThemViPhamRequest {
    private Integer userId;
    private String noiDungViPham;
    private String loaiViPham;
    private Integer baoCaoId; // nullable
    private String ghiChu; // nullable
} 
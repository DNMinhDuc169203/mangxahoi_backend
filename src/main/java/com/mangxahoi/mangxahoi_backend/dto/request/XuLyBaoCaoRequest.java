package com.mangxahoi.mangxahoi_backend.dto.request;

import com.mangxahoi.mangxahoi_backend.enums.TrangThaiBaoCao;
import lombok.Data;

@Data
public class XuLyBaoCaoRequest {
    private TrangThaiBaoCao trangThai;
    private String ghiChuXuLy;
} 
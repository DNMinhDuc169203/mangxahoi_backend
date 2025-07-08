package com.mangxahoi.mangxahoi_backend.admin.dto.response;

import lombok.Data;

@Data
public class ThongTinViPhamNguoiDungDTO {
    private Integer userId;
    private String tenNguoiDung;
    private int tongSoLanViPham;
    private String hinhPhatGanNhat;
    private String trangThaiTaiKhoan;
} 
package com.mangxahoi.mangxahoi_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoiMoiKetBanDaGuiDTO {
    private Integer idLoiMoi; // id của entity KetBan
    private Integer idNguoiNhan;
    private String hoTenNguoiNhan;
    private String anhDaiDienNguoiNhan;
    private String emailNguoiNhan;
} 
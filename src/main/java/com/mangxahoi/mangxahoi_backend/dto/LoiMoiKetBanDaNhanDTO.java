package com.mangxahoi.mangxahoi_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoiMoiKetBanDaNhanDTO {
    private Integer idLoiMoi; // id của entity KetBan
    private Integer idNguoiGui;
    private String hoTenNguoiGui;
    private String anhDaiDienNguoiGui;
    private String emailNguoiGui;
} 
package com.mangxahoi.mangxahoi_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BanBeChungDTO {
    private Integer idNguoiDung1;
    private Integer idNguoiDung2;
    private long soBanBeChung;
    private List<NguoiDungDTO> danhSachBanBeChung;
} 
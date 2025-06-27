// src/main/java/com/mangxahoi/mangxahoi_backend/admin/dto/response/ThongKeResponse.java

package com.mangxahoi.mangxahoi_backend.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeResponse {
    private long tongSoNguoiDung;
    private long nguoiDungMoi;
    private long tongSoBaiViet;
    private long baiVietMoi;
    private long tongSoBinhLuan;
    private long binhLuanMoi;
    private long tongSoBaoCao;
    private long baoCaoChuaXuLy;
    private long nguoiDungBiKhoa;
    private ThongKeTrend trendTuanNay;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThongKeTrend {
        private String hashtagPhoThong;
        private int soLanHashtagPhoThong;
        private int nguoiDungHoatDong;
        private int baiVietXuHuong;
    }
}
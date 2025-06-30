package com.mangxahoi.mangxahoi_backend.dto.request;

import lombok.Data;

@Data
public class PrivacySettingsRequest {
    private Boolean emailCongKhai;
    private Boolean sdtCongKhai;
    private Boolean ngaySinhCongKhai;
    private Boolean gioiTinhCongKhai;
} 
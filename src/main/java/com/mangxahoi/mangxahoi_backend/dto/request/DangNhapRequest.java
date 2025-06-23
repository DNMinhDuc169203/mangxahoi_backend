package com.mangxahoi.mangxahoi_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DangNhapRequest {
    private String emailHoacSoDienThoai;
    private String matKhau;
} 
package com.mangxahoi.mangxahoi_backend.dto.response;

import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DangNhapResponse {
    private String token;
    private NguoiDungDTO nguoiDung;
} 
package com.mangxahoi.mangxahoi_backend.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuenMatKhauRequest {
    @NotEmpty(message = "Email hoặc số điện thoại không được để trống")
    private String emailHoacSoDienThoai;
} 
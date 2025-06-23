package com.mangxahoi.mangxahoi_backend.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class XacThucMatKhauRequest {
    @NotEmpty(message = "Email hoặc số điện thoại không được để trống")
    private String emailHoacSoDienThoai;
    
    @NotEmpty(message = "Mã xác thực không được để trống")
    @Size(min = 6, max = 6, message = "Mã xác thực phải có 6 ký tự")
    private String maXacThuc;
    
    @NotEmpty(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự")
    private String matKhauMoi;
} 
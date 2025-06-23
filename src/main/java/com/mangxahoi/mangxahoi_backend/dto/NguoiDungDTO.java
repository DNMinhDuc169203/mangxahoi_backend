package com.mangxahoi.mangxahoi_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mangxahoi.mangxahoi_backend.enums.CheDoBaiViet;
import com.mangxahoi.mangxahoi_backend.enums.GioiTinh;
import com.mangxahoi.mangxahoi_backend.enums.VaiTro;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NguoiDungDTO {
    private Integer id;
    private String email;
    private String soDienThoai;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String matKhau;
    
    private String hoTen;
    private String tieuSu;
    private LocalDate ngaySinh;
    private GioiTinh gioiTinh;
    private String diaChi;
    private Boolean daXacThuc;
    private Boolean dangHoatDong;
    private CheDoBaiViet mucRiengTu;
    private LocalDateTime ngayTao;
    private LocalDateTime lanDangNhapCuoi;
    private Integer soBanBe;
    private Integer soBaiDang;
    private VaiTro vaiTro;
    private String anhDaiDien;
} 
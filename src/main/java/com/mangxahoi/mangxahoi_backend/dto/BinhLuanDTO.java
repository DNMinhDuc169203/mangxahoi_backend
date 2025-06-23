package com.mangxahoi.mangxahoi_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinhLuanDTO {
    private Integer id;
    private Integer idBaiViet;
    private Integer idNguoiDung;
    private String hoTenNguoiDung;
    private String anhDaiDienNguoiDung;
    private Integer idBinhLuanCha;
    private String noiDung;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
    private Integer soLuotThich;
    private Boolean daThich; // Đánh dấu người dùng hiện tại đã thích bình luận này chưa
    private List<BinhLuanDTO> binhLuanCons;
} 
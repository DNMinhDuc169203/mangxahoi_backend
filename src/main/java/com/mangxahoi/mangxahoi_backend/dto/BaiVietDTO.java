package com.mangxahoi.mangxahoi_backend.dto;

import com.mangxahoi.mangxahoi_backend.enums.CheDoBaiViet;
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
public class BaiVietDTO {
    private Integer id;
    private Integer idNguoiDung;
    private String hoTenNguoiDung;
    private String anhDaiDienNguoiDung;
    private String noiDung;
    private CheDoBaiViet cheDoRiengTu;
    private Boolean dangXuHuong;
    private Integer soLuotXem;
    private Integer soLuotThich;
    private Integer soLuotBinhLuan;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
    private List<String> mediaUrls;
    private List<String> hashtags;
    private Boolean daThich; // Đánh dấu người dùng hiện tại đã thích bài viết này chưa
    private Boolean biAn; // Đánh dấu bài viết đã bị ẩn bởi admin
} 
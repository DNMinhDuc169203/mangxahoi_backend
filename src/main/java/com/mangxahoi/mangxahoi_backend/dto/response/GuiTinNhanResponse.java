 package com.mangxahoi.mangxahoi_backend.dto.response;

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
public class GuiTinNhanResponse {
    private Integer idTinNhan;
    private Integer idCuocTroChuyen;
    private Integer idNguoiGui;
     private String tenNguoiGui;
     private String anhNguoiGui;
    private String noiDung;
    private String loaiTinNhan;
    private String urlTepTin;
    private Boolean daDoc;
    private LocalDateTime ngayTao;
    // Thêm trường mới
    private List<NguoiDocDTO> danhSachNguoiDoc;
}

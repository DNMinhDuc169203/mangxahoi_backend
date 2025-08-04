package com.mangxahoi.mangxahoi_backend.dto.request;

import lombok.Data;

@Data
public class GuiTinNhanRequest {
    private Integer idCuocTroChuyen;
    private Integer idNguoiGui;
    private String noiDung;
    private String loaiTinNhan; 
    private String urlTepTin;
    private String tenNguoiGui;
    private String anhNguoiGui;


}

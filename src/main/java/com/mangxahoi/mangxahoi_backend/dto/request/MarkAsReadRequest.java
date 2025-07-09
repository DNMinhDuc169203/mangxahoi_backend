package com.mangxahoi.mangxahoi_backend.dto.request;

import lombok.Data;

@Data
public class MarkAsReadRequest {
    private Integer idCuocTroChuyen;
    private Integer idNguoiDoc;
    // getter/setter
}
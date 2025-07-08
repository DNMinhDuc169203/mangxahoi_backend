package com.mangxahoi.mangxahoi_backend.dto.request;

import lombok.Data;

@Data
public class TimKiemTinNhanRequest {
    private Integer idCuocTroChuyen;
    private Integer idNguoiThucHien;
    private String tuKhoa;
    private Integer trang;
    private Integer kichThuoc;

}

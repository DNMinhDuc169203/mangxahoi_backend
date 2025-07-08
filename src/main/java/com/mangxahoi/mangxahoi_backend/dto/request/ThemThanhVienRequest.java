package com.mangxahoi.mangxahoi_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemThanhVienRequest {
    private Integer idCuocTroChuyen;
    private List<Integer> idThanhVienMoi;
    private Integer idNguoiThucHien;

    // Getters và Setters
    public Integer getIdCuocTroChuyen() { return idCuocTroChuyen; }
    public void setIdCuocTroChuyen(Integer idCuocTroChuyen) { this.idCuocTroChuyen = idCuocTroChuyen; }
    public List<Integer> getIdThanhVienMoi() { return idThanhVienMoi; }
    public void setIdThanhVienMoi(List<Integer> idThanhVienMoi) { this.idThanhVienMoi = idThanhVienMoi; }
    public Integer getIdNguoiThucHien() { return idNguoiThucHien; }
    public void setIdNguoiThucHien(Integer idNguoiThucHien) { this.idNguoiThucHien = idNguoiThucHien; }
}

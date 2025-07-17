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


}

package com.mangxahoi.mangxahoi_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaoCuocTroChuyenResponse {
    private Integer idCuocTroChuyen;
    private String loai;
    private String tenNhom;
    private String anhNhom;
    private Integer idNguoiTao;
    private List<Integer> idThanhVien;
    private Integer idDoiPhuong;
    private String tenDoiPhuong;
    private String anhDaiDienDoiPhuong;
}

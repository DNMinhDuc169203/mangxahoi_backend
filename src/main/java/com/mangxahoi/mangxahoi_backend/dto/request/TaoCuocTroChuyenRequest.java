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
public class TaoCuocTroChuyenRequest {
    private String loai; // ca_nhan, nhom
    private String tenNhom;
    private String anhNhom;
    private Integer idNguoiTao;
    private List<Integer> idThanhVien;

    // Getters và Setters
}

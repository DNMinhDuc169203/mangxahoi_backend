package com.mangxahoi.mangxahoi_backend.admin.service;

import com.mangxahoi.mangxahoi_backend.entity.ChinhSach;
import com.mangxahoi.mangxahoi_backend.admin.dto.response.ChinhSachDTO;
import java.util.List;

public interface ChinhSachService {
    ChinhSach taoChinhSach(String tieuDe, String noiDung, Integer adminId);
    ChinhSachDTO capNhatChinhSach(Integer id, String tieuDe, String noiDung, Integer adminId);
    List<ChinhSachDTO> layDanhSachChinhSach();
    ChinhSach layChiTietChinhSach(Integer id);
    ChinhSach layChinhSachMoiNhat();
    ChinhSachDTO layChinhSachMoiNhatDTO();
    void xoaChinhSach(Integer id, Integer adminId);
} 
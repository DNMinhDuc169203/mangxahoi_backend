package com.mangxahoi.mangxahoi_backend.admin.service;

import com.mangxahoi.mangxahoi_backend.entity.ChinhSach;
import java.util.List;

public interface ChinhSachService {
    ChinhSach taoChinhSach(String tieuDe, String noiDung, Integer adminId);
    ChinhSach capNhatChinhSach(Integer id, String tieuDe, String noiDung, Integer adminId);
    List<ChinhSach> layDanhSachChinhSach();
    ChinhSach layChiTietChinhSach(Integer id);
    ChinhSach layChinhSachMoiNhat();
} 
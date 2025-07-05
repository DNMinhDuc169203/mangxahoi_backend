package com.mangxahoi.mangxahoi_backend.service;

import com.mangxahoi.mangxahoi_backend.entity.BaoCao;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface BaoCaoService {
    ResponseEntity<?> guiBaoCao(BaoCao baoCao, NguoiDung nguoiDung);
    ResponseEntity<?> layDanhSachBaoCao(Pageable pageable, String trangThai);
    ResponseEntity<?> layBaoCaoNguoiDung(Integer idNguoiDung);
} 
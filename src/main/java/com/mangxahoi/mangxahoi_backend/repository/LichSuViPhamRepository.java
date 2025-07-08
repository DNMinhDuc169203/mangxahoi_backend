package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.LichSuViPham;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuViPhamRepository extends JpaRepository<LichSuViPham, Integer> {
    List<LichSuViPham> findByNguoiDung(NguoiDung nguoiDung);
    long countByNguoiDung(NguoiDung nguoiDung);
} 
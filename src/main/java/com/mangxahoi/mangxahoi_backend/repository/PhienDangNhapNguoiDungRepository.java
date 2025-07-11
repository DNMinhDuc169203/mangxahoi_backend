package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.PhienDangNhapNguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhienDangNhapNguoiDungRepository extends JpaRepository<PhienDangNhapNguoiDung, Integer> {
    Optional<PhienDangNhapNguoiDung> findByMaPhien(String maPhien);
    
    void deleteByNguoiDung(NguoiDung nguoiDung);
    void deleteByMaPhien(String maPhien);
} 
package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.PhienDangNhapNguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhienDangNhapNguoiDungRepository extends JpaRepository<PhienDangNhapNguoiDung, Integer> {
    @Query("SELECT p FROM PhienDangNhapNguoiDung p WHERE p.maPhien = :maPhien")
    Optional<PhienDangNhapNguoiDung> findByMaPhien(@Param("maPhien") String maPhien);
    
    void deleteByNguoiDung(NguoiDung nguoiDung);
    void deleteByMaPhien(String maPhien);
} 
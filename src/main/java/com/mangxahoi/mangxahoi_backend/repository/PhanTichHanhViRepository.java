package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.PhanTichHanhVi;
import com.mangxahoi.mangxahoi_backend.enums.MucDoTuongTac;
import com.mangxahoi.mangxahoi_backend.enums.MucDoPhoVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhanTichHanhViRepository extends JpaRepository<PhanTichHanhVi, Integer> {
    List<PhanTichHanhVi> findByNguoiDung(NguoiDung nguoiDung);
    
    Optional<PhanTichHanhVi> findByNguoiDungAndNgayPhanTich(NguoiDung nguoiDung, LocalDate ngayPhanTich);
    
    List<PhanTichHanhVi> findByNguoiDungAndNgayPhanTichBetween(
            NguoiDung nguoiDung, 
            LocalDate startDate, 
            LocalDate endDate);
    
    List<PhanTichHanhVi> findByMucDoTuongTac(MucDoTuongTac mucDoTuongTac);
    
    List<PhanTichHanhVi> findByMucDoPhoVien(MucDoPhoVien mucDoPhoVien);
    
    @Query("SELECT p FROM PhanTichHanhVi p WHERE p.diemHoatDong >= :minDiem ORDER BY p.diemHoatDong DESC")
    Page<PhanTichHanhVi> findActiveUsers(@Param("minDiem") Integer minDiem, Pageable pageable);
    
    @Query("SELECT p FROM PhanTichHanhVi p WHERE p.nguoiDung = :nguoiDung ORDER BY p.ngayPhanTich DESC")
    List<PhanTichHanhVi> findRecentAnalytics(@Param("nguoiDung") NguoiDung nguoiDung, Pageable pageable);
    
    @Query("SELECT AVG(p.diemHoatDong) FROM PhanTichHanhVi p WHERE p.nguoiDung = :nguoiDung AND p.ngayPhanTich BETWEEN :startDate AND :endDate")
    Double getAverageActivityScore(
            @Param("nguoiDung") NguoiDung nguoiDung, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
    
    void deleteByNguoiDung(NguoiDung nguoiDung);
    
    @Procedure(name = "analyze_user_behavior")
    void analyzeUserBehavior(@Param("user_id") Integer userId);
} 
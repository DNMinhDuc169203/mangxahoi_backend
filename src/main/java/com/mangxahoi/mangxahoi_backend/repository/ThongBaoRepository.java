package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.ThongBao;
import com.mangxahoi.mangxahoi_backend.enums.LoaiThongBao;
import com.mangxahoi.mangxahoi_backend.enums.MucDoUuTien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ThongBaoRepository extends JpaRepository<ThongBao, Integer> {
    List<ThongBao> findByNguoiNhan(NguoiDung nguoiNhan);
    
    Page<ThongBao> findByNguoiNhanAndDaDocAndDaXoa(
            NguoiDung nguoiNhan, 
            Boolean daDoc, 
            Boolean daXoa, 
            Pageable pageable);
    
    List<ThongBao> findByNguoiNhanAndLoai(NguoiDung nguoiNhan, LoaiThongBao loai);
    
    @Query("SELECT t FROM ThongBao t WHERE t.nguoiNhan = :nguoiNhan AND t.daDoc = false AND t.daXoa = false ORDER BY t.ngayTao DESC")
    Page<ThongBao> findUnreadNotifications(@Param("nguoiNhan") NguoiDung nguoiNhan, Pageable pageable);
    
    @Query("SELECT COUNT(t) FROM ThongBao t WHERE t.nguoiNhan = :nguoiNhan AND t.daDoc = false AND t.daXoa = false")
    long countUnreadNotifications(@Param("nguoiNhan") NguoiDung nguoiNhan);
    
    @Query("SELECT t FROM ThongBao t WHERE t.nguoiNhan = :nguoiNhan AND t.mucDoUuTien = :mucDoUuTien AND t.daXoa = false ORDER BY t.ngayTao DESC")
    List<ThongBao> findByNguoiNhanAndMucDoUuTien(
            @Param("nguoiNhan") NguoiDung nguoiNhan, 
            @Param("mucDoUuTien") MucDoUuTien mucDoUuTien);
    
    @Modifying
    @Transactional
    @Query("UPDATE ThongBao t SET t.daDoc = true WHERE t.nguoiNhan = :nguoiNhan AND t.daDoc = false")
    void markAllAsRead(@Param("nguoiNhan") NguoiDung nguoiNhan);
    
    @Modifying
    @Transactional
    @Query("UPDATE ThongBao t SET t.daXoa = true WHERE t.nguoiNhan = :nguoiNhan AND t.id IN :ids")
    void markAsDeleted(@Param("nguoiNhan") NguoiDung nguoiNhan, @Param("ids") List<Integer> ids);
    
    @Query("SELECT t FROM ThongBao t WHERE t.nguoiNhan = :nguoiNhan AND t.ngayTao BETWEEN :startDate AND :endDate ORDER BY t.ngayTao DESC")
    List<ThongBao> findByNguoiNhanAndDateRange(
            @Param("nguoiNhan") NguoiDung nguoiNhan, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    void deleteByNguoiNhan(NguoiDung nguoiNhan);
} 
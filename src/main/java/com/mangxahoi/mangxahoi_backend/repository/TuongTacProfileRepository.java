package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.TuongTacProfile;
import com.mangxahoi.mangxahoi_backend.enums.LoaiTuongTacProfile;
import com.mangxahoi.mangxahoi_backend.enums.NguonTruyCapProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TuongTacProfileRepository extends JpaRepository<TuongTacProfile, Integer> {
    List<TuongTacProfile> findByNguoiXem(NguoiDung nguoiXem);
    
    List<TuongTacProfile> findByProfileDuocXem(NguoiDung profileDuocXem);
    
    List<TuongTacProfile> findByNguoiXemAndProfileDuocXem(NguoiDung nguoiXem, NguoiDung profileDuocXem);
    
    List<TuongTacProfile> findByLoaiTuongTac(LoaiTuongTacProfile loaiTuongTac);
    
    List<TuongTacProfile> findByNguonTruyCap(NguonTruyCapProfile nguonTruyCap);
    
    @Query("SELECT t FROM TuongTacProfile t WHERE t.profileDuocXem = :nguoiDung AND t.ngayTao BETWEEN :startDate AND :endDate ORDER BY t.ngayTao DESC")
    Page<TuongTacProfile> findProfileViewsInDateRange(
            @Param("nguoiDung") NguoiDung nguoiDung, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    @Query("SELECT COUNT(t) FROM TuongTacProfile t WHERE t.profileDuocXem = :nguoiDung AND t.ngayTao >= :startDate")
    long countRecentProfileViews(@Param("nguoiDung") NguoiDung nguoiDung, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT SUM(t.thoiGianXem) FROM TuongTacProfile t WHERE t.profileDuocXem = :nguoiDung AND t.ngayTao >= :startDate")
    Integer getTotalViewTimeForProfile(@Param("nguoiDung") NguoiDung nguoiDung, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT t.nguoiXem, COUNT(t) FROM TuongTacProfile t WHERE t.profileDuocXem = :nguoiDung GROUP BY t.nguoiXem ORDER BY COUNT(t) DESC")
    Page<Object[]> findTopViewersForProfile(@Param("nguoiDung") NguoiDung nguoiDung, Pageable pageable);
    
    void deleteByNguoiXem(NguoiDung nguoiXem);
    
    void deleteByProfileDuocXem(NguoiDung profileDuocXem);
} 
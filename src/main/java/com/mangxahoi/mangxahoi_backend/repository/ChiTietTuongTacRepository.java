package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.BaiViet;
import com.mangxahoi.mangxahoi_backend.entity.BinhLuan;
import com.mangxahoi.mangxahoi_backend.entity.ChiTietTuongTac;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.enums.LoaiTuongTac;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChiTietTuongTacRepository extends JpaRepository<ChiTietTuongTac, Integer> {
    List<ChiTietTuongTac> findByNguoi1(NguoiDung nguoi1);
    
    List<ChiTietTuongTac> findByNguoi2(NguoiDung nguoi2);
    
    List<ChiTietTuongTac> findByNguoi1AndNguoi2(NguoiDung nguoi1, NguoiDung nguoi2);
    
    List<ChiTietTuongTac> findByLoaiTuongTac(LoaiTuongTac loaiTuongTac);
    
    List<ChiTietTuongTac> findByBaiViet(BaiViet baiViet);
    
    List<ChiTietTuongTac> findByBinhLuan(BinhLuan binhLuan);
    
    @Query("SELECT c FROM ChiTietTuongTac c WHERE c.nguoi1 = :nguoi1 AND c.nguoi2 = :nguoi2 AND c.ngayTao BETWEEN :startDate AND :endDate")
    List<ChiTietTuongTac> findByNguoi1AndNguoi2BetweenDates(
            @Param("nguoi1") NguoiDung nguoi1, 
            @Param("nguoi2") NguoiDung nguoi2, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT c FROM ChiTietTuongTac c WHERE c.nguoi1 = :nguoi1 AND c.ngayTao >= :startDate ORDER BY c.diemTuongTac DESC")
    Page<ChiTietTuongTac> findRecentInteractionsByNguoi1(
            @Param("nguoi1") NguoiDung nguoi1,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable);
    
    @Query("SELECT SUM(c.diemTuongTac) FROM ChiTietTuongTac c WHERE c.nguoi1 = :nguoi1 AND c.nguoi2 = :nguoi2")
    Integer getTotalInteractionPoints(@Param("nguoi1") NguoiDung nguoi1, @Param("nguoi2") NguoiDung nguoi2);
    
    void deleteByNguoi1(NguoiDung nguoi1);
    
    void deleteByNguoi2(NguoiDung nguoi2);
    
    void deleteByBaiViet(BaiViet baiViet);
    
    void deleteByBinhLuan(BinhLuan binhLuan);
} 
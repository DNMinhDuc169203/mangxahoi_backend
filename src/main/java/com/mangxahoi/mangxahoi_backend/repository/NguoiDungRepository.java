package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {
    Optional<NguoiDung> findByEmail(String email);
    Optional<NguoiDung> findBySoDienThoai(String soDienThoai);
    boolean existsByEmail(String email);
    boolean existsBySoDienThoai(String soDienThoai);
    
    
    @Query("SELECT n FROM NguoiDung n WHERE n.hoTen LIKE %:hoTen% AND n.biXoaMem = false")
    Page<NguoiDung> findByHoTenContainingIgnoreCase(@Param("hoTen") String hoTen, Pageable pageable);

    Page<NguoiDung> findBySoDienThoaiContaining(String soDienThoai, Pageable pageable);
    long countByBiTamKhoaTrue();
    long countByNgayTaoAfter(java.time.LocalDateTime from);
    // Thêm method cho scheduler tự động mở khóa
    List<NguoiDung> findAllByBiTamKhoaTrueAndNgayMoKhoaIsNotNull();
    // Lấy tất cả user thường (không bao gồm admin)
    @Query("SELECT n FROM NguoiDung n WHERE n.vaiTro <> com.mangxahoi.mangxahoi_backend.enums.VaiTro.quan_tri_vien")
    Page<NguoiDung> findAllUserKhongPhaiAdmin(Pageable pageable);
} 
package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {
    Optional<NguoiDung> findByEmail(String email);
    Optional<NguoiDung> findBySoDienThoai(String soDienThoai);
    boolean existsByEmail(String email);
    boolean existsBySoDienThoai(String soDienThoai);
    
    /**
     * Tìm kiếm người dùng theo họ tên (tìm kiếm mờ)
     * 
     * @param hoTen Họ tên cần tìm kiếm
     * @param pageable Thông tin phân trang
     * @return Danh sách người dùng phù hợp
     */
    @Query("SELECT n FROM NguoiDung n WHERE n.hoTen LIKE %:hoTen% AND n.biXoaMem = false")
    Page<NguoiDung> findByHoTenContainingIgnoreCase(@Param("hoTen") String hoTen, Pageable pageable);

    Page<NguoiDung> findBySoDienThoaiContaining(String soDienThoai, Pageable pageable);
    long countByBiTamKhoaTrue();
    long countByNgayTaoAfter(java.time.LocalDateTime from);
} 
package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.CuocTroChuyen;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.enums.LoaiCuocTroChuyen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuocTroChuyenRepository extends JpaRepository<CuocTroChuyen, Integer> {
    List<CuocTroChuyen> findByNguoiTao(NguoiDung nguoiTao);
    
    List<CuocTroChuyen> findByNguoiThamGia(NguoiDung nguoiThamGia);
    
    Optional<CuocTroChuyen> findByNguoiTaoAndNguoiThamGiaAndLoai(NguoiDung nguoiTao, NguoiDung nguoiThamGia, LoaiCuocTroChuyen loai);
    
    @Query("SELECT c FROM CuocTroChuyen c WHERE c.loai = :loai AND (c.nguoiTao = :nguoiDung OR c.nguoiThamGia = :nguoiDung) ORDER BY c.tinNhanCuoi DESC")
    Page<CuocTroChuyen> findConversationsByUserAndType(
            @Param("nguoiDung") NguoiDung nguoiDung,
            @Param("loai") LoaiCuocTroChuyen loai,
            Pageable pageable);
    
    @Query("SELECT c FROM CuocTroChuyen c WHERE c.loai = 'ca_nhan' AND ((c.nguoiTao = :nguoiDung1 AND c.nguoiThamGia = :nguoiDung2) OR (c.nguoiTao = :nguoiDung2 AND c.nguoiThamGia = :nguoiDung1))")
    Optional<CuocTroChuyen> findPrivateConversationBetweenUsers(
            @Param("nguoiDung1") NguoiDung nguoiDung1,
            @Param("nguoiDung2") NguoiDung nguoiDung2);
    
    @Query("SELECT c FROM CuocTroChuyen c WHERE c.loai = 'nhom' AND c.tenNhom LIKE %:keyword%")
    Page<CuocTroChuyen> searchGroupConversationsByName(
            @Param("keyword") String keyword,
            Pageable pageable);
} 
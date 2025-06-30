package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.KetBan;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.enums.LoaiKetBan;
import com.mangxahoi.mangxahoi_backend.enums.TrangThaiKetBan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KetBanRepository extends JpaRepository<KetBan, Integer> {
    List<KetBan> findByNguoiGui(NguoiDung nguoiGui);
    
    List<KetBan> findByNguoiNhan(NguoiDung nguoiNhan);
    
    Optional<KetBan> findByNguoiGuiAndNguoiNhan(NguoiDung nguoiGui, NguoiDung nguoiNhan);
    
    Page<KetBan> findByNguoiGuiAndTrangThai(NguoiDung nguoiGui, TrangThaiKetBan trangThai, Pageable pageable);
    
    Page<KetBan> findByNguoiNhanAndTrangThai(NguoiDung nguoiNhan, TrangThaiKetBan trangThai, Pageable pageable);
    
    @Query("SELECT k FROM KetBan k WHERE k.trangThai = 'ban_be' AND (k.nguoiGui = :nguoiDung OR k.nguoiNhan = :nguoiDung)")
    Page<KetBan> findFriends(@Param("nguoiDung") NguoiDung nguoiDung, Pageable pageable);
    
    @Query("SELECT k FROM KetBan k WHERE k.trangThai = 'ban_be' AND (k.nguoiGui = :nguoiDung OR k.nguoiNhan = :nguoiDung)")
    List<KetBan> findAllFriends(@Param("nguoiDung") NguoiDung nguoiDung);
    
    @Query("SELECT COUNT(k) FROM KetBan k WHERE k.trangThai = 'ban_be' AND (k.nguoiGui = :nguoiDung OR k.nguoiNhan = :nguoiDung)")
    long countFriends(@Param("nguoiDung") NguoiDung nguoiDung);
    
    @Query("SELECT COUNT(k) FROM KetBan k WHERE k.trangThai = 'cho_chap_nhan' AND k.nguoiNhan = :nguoiDung")
    long countPendingFriendRequests(@Param("nguoiDung") NguoiDung nguoiDung);
    
    @Query("SELECT k FROM KetBan k WHERE k.trangThai = 'ban_be' AND ((k.nguoiGui = :nguoiDung1 AND k.nguoiNhan = :nguoiDung2) OR (k.nguoiGui = :nguoiDung2 AND k.nguoiNhan = :nguoiDung1))")
    Optional<KetBan> areFriends(@Param("nguoiDung1") NguoiDung nguoiDung1, @Param("nguoiDung2") NguoiDung nguoiDung2);
    
    @Query("SELECT k FROM KetBan k WHERE (k.nguoiGui = :nguoiDung1 AND k.nguoiNhan = :nguoiDung2) OR (k.nguoiGui = :nguoiDung2 AND k.nguoiNhan = :nguoiDung1)")
    Optional<KetBan> findRelationship(@Param("nguoiDung1") NguoiDung nguoiDung1, @Param("nguoiDung2") NguoiDung nguoiDung2);
    
    void deleteByNguoiGui(NguoiDung nguoiGui);
    
    void deleteByNguoiNhan(NguoiDung nguoiNhan);
} 
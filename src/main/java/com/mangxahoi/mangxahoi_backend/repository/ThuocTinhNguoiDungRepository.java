package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.ThuocTinhNguoiDung;
import com.mangxahoi.mangxahoi_backend.enums.NhomTuoi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThuocTinhNguoiDungRepository extends JpaRepository<ThuocTinhNguoiDung, Integer> {
    Optional<ThuocTinhNguoiDung> findByNguoiDung(NguoiDung nguoiDung);
    
    List<ThuocTinhNguoiDung> findByTinhThanh(String tinhThanh);
    
    List<ThuocTinhNguoiDung> findByTinhThanhAndQuanHuyen(String tinhThanh, String quanHuyen);
    
    List<ThuocTinhNguoiDung> findByNhomTuoi(NhomTuoi nhomTuoi);
    
    List<ThuocTinhNguoiDung> findByNgheNghiep(String ngheNghiep);
    
    @Query("SELECT t FROM ThuocTinhNguoiDung t WHERE t.tinhThanh = :tinhThanh AND t.nhomTuoi = :nhomTuoi")
    List<ThuocTinhNguoiDung> findByLocationAndAgeGroup(
            @Param("tinhThanh") String tinhThanh, 
            @Param("nhomTuoi") NhomTuoi nhomTuoi);
    
    @Query("SELECT t FROM ThuocTinhNguoiDung t WHERE t.soThich LIKE %:soThich%")
    List<ThuocTinhNguoiDung> findBySoThichContaining(@Param("soThich") String soThich);
    
    void deleteByNguoiDung(NguoiDung nguoiDung);
} 
package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.CuocTroChuyen;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.ThanhVienCuocTroChuyen;
import com.mangxahoi.mangxahoi_backend.enums.VaiTroThanhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThanhVienCuocTroChuyenRepository extends JpaRepository<ThanhVienCuocTroChuyen, Integer> {
    List<ThanhVienCuocTroChuyen> findByCuocTroChuyen(CuocTroChuyen cuocTroChuyen);
    
    List<ThanhVienCuocTroChuyen> findByNguoiDung(NguoiDung nguoiDung);
    
    Optional<ThanhVienCuocTroChuyen> findByCuocTroChuyenAndNguoiDung(CuocTroChuyen cuocTroChuyen, NguoiDung nguoiDung);
    
    List<ThanhVienCuocTroChuyen> findByCuocTroChuyenAndVaiTro(CuocTroChuyen cuocTroChuyen, VaiTroThanhVien vaiTro);
    
    @Query("SELECT COUNT(t) FROM ThanhVienCuocTroChuyen t WHERE t.cuocTroChuyen = :cuocTroChuyen")
    long countByCuocTroChuyen(@Param("cuocTroChuyen") CuocTroChuyen cuocTroChuyen);
    
    boolean existsByCuocTroChuyenAndNguoiDung(CuocTroChuyen cuocTroChuyen, NguoiDung nguoiDung);
    
    void deleteByCuocTroChuyen(CuocTroChuyen cuocTroChuyen);
    
    void deleteByNguoiDung(NguoiDung nguoiDung);
    
    void deleteByCuocTroChuyenAndNguoiDung(CuocTroChuyen cuocTroChuyen, NguoiDung nguoiDung);

    void deleteAllByCuocTroChuyen(CuocTroChuyen cuocTroChuyen);
} 
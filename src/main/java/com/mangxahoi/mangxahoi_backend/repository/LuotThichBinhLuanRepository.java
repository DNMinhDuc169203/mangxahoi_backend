package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.BinhLuan;
import com.mangxahoi.mangxahoi_backend.entity.LuotThichBinhLuan;
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
public interface LuotThichBinhLuanRepository extends JpaRepository<LuotThichBinhLuan, Integer> {
    List<LuotThichBinhLuan> findByBinhLuan(BinhLuan binhLuan);
    
    Page<LuotThichBinhLuan> findByBinhLuan(BinhLuan binhLuan, Pageable pageable);
    
    List<LuotThichBinhLuan> findByNguoiDung(NguoiDung nguoiDung);
    

    Optional<LuotThichBinhLuan> findByNguoiDungAndBinhLuan(NguoiDung nguoiDung, BinhLuan binhLuan);
    
    @Query("SELECT l FROM LuotThichBinhLuan l WHERE l.nguoiDung.id = :idNguoiDung AND l.binhLuan.id = :idBinhLuan AND l.trangThaiThich = true")
    Optional<LuotThichBinhLuan> findByNguoiDungIdAndBinhLuanIdAndTrangThaiThichTrue(
            @Param("idNguoiDung") Integer idNguoiDung, 
            @Param("idBinhLuan") Integer idBinhLuan);
    
    
    long countByBinhLuanAndTrangThaiThichTrue(BinhLuan binhLuan);
    
   
    @Query("SELECT l.nguoiDung FROM LuotThichBinhLuan l WHERE l.binhLuan = :binhLuan AND l.trangThaiThich = true")
    List<NguoiDung> findNguoiDungsByBinhLuanAndTrangThaiThichTrue(@Param("binhLuan") BinhLuan binhLuan);
    
    boolean existsByNguoiDungAndBinhLuanAndTrangThaiThich(NguoiDung nguoiDung, BinhLuan binhLuan, Boolean trangThaiThich);
    
    
    void deleteByBinhLuan(BinhLuan binhLuan);
    
    void deleteByNguoiDung(NguoiDung nguoiDung);
} 
package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.BaiViet;
import com.mangxahoi.mangxahoi_backend.entity.LuotThichBaiViet;
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
public interface LuotThichBaiVietRepository extends JpaRepository<LuotThichBaiViet, Integer> {
    List<LuotThichBaiViet> findByBaiViet(BaiViet baiViet);
    
    Page<LuotThichBaiViet> findByBaiViet(BaiViet baiViet, Pageable pageable);
    
    List<LuotThichBaiViet> findByNguoiDung(NguoiDung nguoiDung);
    
    Optional<LuotThichBaiViet> findByNguoiDungAndBaiViet(NguoiDung nguoiDung, BaiViet baiViet);
    
    @Query("SELECT l FROM LuotThichBaiViet l WHERE l.nguoiDung.id = :idNguoiDung AND l.baiViet.id = :idBaiViet AND l.trangThaiThich = true")
    Optional<LuotThichBaiViet> findByNguoiDungIdAndBaiVietIdAndTrangThaiThichTrue(
            @Param("idNguoiDung") Integer idNguoiDung, 
            @Param("idBaiViet") Integer idBaiViet);
 
    long countByBaiVietAndTrangThaiThichTrue(BaiViet baiViet);
    
   
    @Query("SELECT l.nguoiDung FROM LuotThichBaiViet l WHERE l.baiViet = :baiViet AND l.trangThaiThich = true")
    List<NguoiDung> findNguoiDungsByBaiVietAndTrangThaiThichTrue(@Param("baiViet") BaiViet baiViet);
    
    boolean existsByNguoiDungAndBaiVietAndTrangThaiThich(NguoiDung nguoiDung, BaiViet baiViet, Boolean trangThaiThich);
    
    void deleteByBaiViet(BaiViet baiViet);
    
    void deleteByNguoiDung(NguoiDung nguoiDung);
} 
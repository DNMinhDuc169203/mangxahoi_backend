package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.BaiViet;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.enums.CheDoBaiViet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaiVietRepository extends JpaRepository<BaiViet, Integer> {
    Page<BaiViet> findByNguoiDungAndCheDoRiengTu(NguoiDung nguoiDung, CheDoBaiViet cheDoRiengTu, Pageable pageable);
    
    Page<BaiViet> findByNguoiDung(NguoiDung nguoiDung, Pageable pageable);
    
    @Query("SELECT b FROM BaiViet b WHERE b.cheDoRiengTu = 'CONG_KHAI' ORDER BY b.ngayTao DESC")
    Page<BaiViet> findAllPublicPosts(Pageable pageable);
    
    @Query("SELECT b FROM BaiViet b WHERE b.dangXuHuong = true AND b.cheDoRiengTu = 'CONG_KHAI' ORDER BY b.soLuotThich DESC, b.soLuotBinhLuan DESC")
    Page<BaiViet> findTrendingPosts(Pageable pageable);
    
    @Query("SELECT b FROM BaiViet b JOIN b.hashtags h WHERE h.ten = :tenHashtag AND b.cheDoRiengTu = 'CONG_KHAI'")
    Page<BaiViet> findByHashtag(@Param("tenHashtag") String tenHashtag, Pageable pageable);
} 
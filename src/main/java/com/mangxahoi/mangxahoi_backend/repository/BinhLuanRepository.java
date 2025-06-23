package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.BaiViet;
import com.mangxahoi.mangxahoi_backend.entity.BinhLuan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BinhLuanRepository extends JpaRepository<BinhLuan, Integer> {
    Page<BinhLuan> findByBaiVietAndBinhLuanChaIsNull(BaiViet baiViet, Pageable pageable);
    
    List<BinhLuan> findByBinhLuanCha(BinhLuan binhLuanCha);
    
    @Query("SELECT COUNT(b) FROM BinhLuan b WHERE b.baiViet.id = :baiVietId")
    long countByBaiVietId(@Param("baiVietId") Integer baiVietId);
    
    @Query("SELECT b FROM BinhLuan b WHERE b.baiViet.id = :baiVietId AND b.binhLuanCha IS NULL ORDER BY b.ngayTao DESC")
    Page<BinhLuan> findParentCommentsByBaiVietId(@Param("baiVietId") Integer baiVietId, Pageable pageable);
} 
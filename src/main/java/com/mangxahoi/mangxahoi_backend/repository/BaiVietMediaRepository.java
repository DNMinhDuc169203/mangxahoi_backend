package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.BaiViet;
import com.mangxahoi.mangxahoi_backend.entity.BaiVietMedia;
import com.mangxahoi.mangxahoi_backend.enums.LoaiMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaiVietMediaRepository extends JpaRepository<BaiVietMedia, Integer> {
    List<BaiVietMedia> findByBaiViet(BaiViet baiViet);
    
    List<BaiVietMedia> findByBaiVietAndLoaiMedia(BaiViet baiViet, LoaiMedia loaiMedia);
    
    void deleteByBaiViet(BaiViet baiViet);
} 
package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.LichSuXuLyBaiViet;
import com.mangxahoi.mangxahoi_backend.entity.BaiViet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuXuLyBaiVietRepository extends JpaRepository<LichSuXuLyBaiViet, Integer> {
    List<LichSuXuLyBaiViet> findByBaiViet(BaiViet baiViet);
    List<LichSuXuLyBaiViet> findByAdminXuLy_Id(Integer adminId);
} 
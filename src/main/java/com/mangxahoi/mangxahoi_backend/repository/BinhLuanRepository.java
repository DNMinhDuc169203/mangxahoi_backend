package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.BaiViet;
import com.mangxahoi.mangxahoi_backend.entity.BinhLuan;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BinhLuanRepository extends JpaRepository<BinhLuan, Integer> {

    Page<BinhLuan> findByBaiViet(BaiViet baiViet, Pageable pageable);
    Page<BinhLuan> findByBaiVietAndBinhLuanChaIsNull(BaiViet baiViet, Pageable pageable);
    Page<BinhLuan> findByBinhLuanCha(BinhLuan binhLuanCha, Pageable pageable);
    long countByBinhLuanCha(BinhLuan binhLuanCha);
    long countByBaiViet(BaiViet baiViet);
    Page<BinhLuan> findByNguoiDung(NguoiDung nguoiDung, Pageable pageable);
    void deleteByBaiViet(BaiViet baiViet);
    long countByNgayTaoAfter(java.time.LocalDateTime from);
} 
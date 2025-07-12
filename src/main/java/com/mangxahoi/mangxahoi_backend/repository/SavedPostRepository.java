package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.SavedPost;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.BaiViet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedPostRepository extends JpaRepository<SavedPost, Long> {
    List<SavedPost> findByNguoiDung(NguoiDung nguoiDung);
    Optional<SavedPost> findByNguoiDungAndBaiViet(NguoiDung nguoiDung, BaiViet baiViet);
    void deleteByNguoiDungAndBaiViet(NguoiDung nguoiDung, BaiViet baiViet);
} 
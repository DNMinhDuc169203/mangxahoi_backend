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
    
    /**
     * Tìm tất cả bình luận của bài viết
     * 
     * @param baiViet Bài viết
     * @param pageable Phân trang
     * @return Danh sách bình luận
     */
    Page<BinhLuan> findByBaiViet(BaiViet baiViet, Pageable pageable);
    
    /**
     * Tìm tất cả bình luận gốc của bài viết (không có bình luận cha)
     * 
     * @param baiViet Bài viết
     * @param pageable Phân trang
     * @return Danh sách bình luận gốc
     */
    Page<BinhLuan> findByBaiVietAndBinhLuanChaIsNull(BaiViet baiViet, Pageable pageable);
    
    /**
     * Tìm tất cả bình luận phản hồi của một bình luận
     * 
     * @param binhLuanCha Bình luận cha
     * @param pageable Phân trang
     * @return Danh sách bình luận phản hồi
     */
    Page<BinhLuan> findByBinhLuanCha(BinhLuan binhLuanCha, Pageable pageable);
    
    /**
     * Đếm số lượng bình luận phản hồi của một bình luận
     * 
     * @param binhLuanCha Bình luận cha
     * @return Số lượng bình luận phản hồi
     */
    long countByBinhLuanCha(BinhLuan binhLuanCha);
    
    /**
     * Đếm số lượng bình luận của bài viết
     * 
     * @param baiViet Bài viết
     * @return Số lượng bình luận
     */
    long countByBaiViet(BaiViet baiViet);
    
    /**
     * Tìm tất cả bình luận của người dùng
     * 
     * @param nguoiDung Người dùng
     * @param pageable Phân trang
     * @return Danh sách bình luận
     */
    Page<BinhLuan> findByNguoiDung(NguoiDung nguoiDung, Pageable pageable);
    
    /**
     * Xóa tất cả bình luận của bài viết
     * 
     * @param baiViet Bài viết
     */
    void deleteByBaiViet(BaiViet baiViet);
} 
package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.BaiViet;
import com.mangxahoi.mangxahoi_backend.entity.BaiVietHashtag;
import com.mangxahoi.mangxahoi_backend.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaiVietHashtagRepository extends JpaRepository<BaiVietHashtag, Integer> {
    
    /**
     * Tìm liên kết giữa bài viết và hashtag
     * 
     * @param baiViet Bài viết
     * @param hashtag Hashtag
     * @return Optional chứa liên kết nếu tồn tại
     */
    Optional<BaiVietHashtag> findByBaiVietAndHashtag(BaiViet baiViet, Hashtag hashtag);
    
    /**
     * Tìm tất cả liên kết theo bài viết
     * 
     * @param baiViet Bài viết
     * @return Danh sách liên kết
     */
    List<BaiVietHashtag> findByBaiViet(BaiViet baiViet);
    
    /**
     * Tìm tất cả liên kết theo hashtag
     * 
     * @param hashtag Hashtag
     * @return Danh sách liên kết
     */
    List<BaiVietHashtag> findByHashtag(Hashtag hashtag);
    
    /**
     * Xóa tất cả liên kết của bài viết
     * 
     * @param baiViet Bài viết
     */
    void deleteByBaiViet(BaiViet baiViet);
    
    /**
     * Xóa liên kết giữa bài viết và hashtag
     * 
     * @param baiViet Bài viết
     * @param hashtag Hashtag
     */
    void deleteByBaiVietAndHashtag(BaiViet baiViet, Hashtag hashtag);
} 
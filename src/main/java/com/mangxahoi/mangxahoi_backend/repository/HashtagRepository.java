package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.Hashtag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Integer> {
    
    /**
     * Tìm hashtag theo tên
     * 
     * @param ten Tên hashtag
     * @return Optional chứa hashtag nếu tồn tại
     */
    Optional<Hashtag> findByTen(String ten);
    
    /**
     * Tìm tất cả hashtag đang xu hướng
     * 
     * @param pageable Phân trang
     * @return Danh sách hashtag xu hướng
     */
    Page<Hashtag> findByDangXuHuongTrueOrderBySoLanSuDungDesc(Pageable pageable);
    
    /**
     * Tìm hashtag theo tên chứa từ khóa
     * 
     * @param keyword Từ khóa
     * @param pageable Phân trang
     * @return Danh sách hashtag
     */
    Page<Hashtag> findByTenContaining(String keyword, Pageable pageable);
    
    /**
     * Tìm top hashtag được sử dụng nhiều nhất
     * 
     * @param limit Số lượng tối đa
     * @return Danh sách hashtag
     */
    @Query("SELECT h FROM Hashtag h ORDER BY h.soLanSuDung DESC")
    List<Hashtag> findTopHashtags(Pageable pageable);
} 
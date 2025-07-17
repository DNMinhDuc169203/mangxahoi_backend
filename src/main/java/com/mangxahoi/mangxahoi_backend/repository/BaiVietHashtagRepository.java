package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.BaiViet;
import com.mangxahoi.mangxahoi_backend.entity.BaiVietHashtag;
import com.mangxahoi.mangxahoi_backend.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BaiVietHashtagRepository extends JpaRepository<BaiVietHashtag, Integer> {
  
    Optional<BaiVietHashtag> findByBaiVietAndHashtag(BaiViet baiViet, Hashtag hashtag);
    
    List<BaiVietHashtag> findByBaiViet(BaiViet baiViet);
    
    List<BaiVietHashtag> findByHashtag(Hashtag hashtag);
    
    void deleteByBaiViet(BaiViet baiViet);
    
    void deleteByBaiVietAndHashtag(BaiViet baiViet, Hashtag hashtag);

    List<BaiVietHashtag> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
} 
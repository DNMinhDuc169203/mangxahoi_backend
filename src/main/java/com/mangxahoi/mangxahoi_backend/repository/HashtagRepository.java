package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.Hashtag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Integer> {
    Optional<Hashtag> findByTen(String ten);
    
    boolean existsByTen(String ten);
    
    @Query("SELECT h FROM Hashtag h WHERE h.dangXuHuong = true ORDER BY h.soLanSuDung DESC")
    Page<Hashtag> findTrendingHashtags(Pageable pageable);
    
    Page<Hashtag> findByTenContainingOrderBySoLanSuDungDesc(String ten, Pageable pageable);
} 
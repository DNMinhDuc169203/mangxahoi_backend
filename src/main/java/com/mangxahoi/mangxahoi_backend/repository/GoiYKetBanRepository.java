package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.LichSuGoiY;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GoiYKetBanRepository extends JpaRepository<LichSuGoiY, Integer> {
    
    @Procedure(procedureName = "calculate_friend_suggestions")
    void calculateFriendSuggestions(@Param("user_id") Integer userId);
} 
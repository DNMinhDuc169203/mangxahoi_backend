package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.ChinhSach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChinhSachRepository extends JpaRepository<ChinhSach, Integer> {
    ChinhSach findTopByOrderByNgayCapNhatDesc();
} 
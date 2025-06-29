package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.ThongBao;
import com.mangxahoi.mangxahoi_backend.entity.ThongBaoHeThong;
import com.mangxahoi.mangxahoi_backend.enums.LoaiHeThong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThongBaoHeThongRepository extends JpaRepository<ThongBaoHeThong, Integer> {
    List<ThongBaoHeThong> findByThongBao(ThongBao thongBao);
    
    List<ThongBaoHeThong> findByLoaiHeThong(LoaiHeThong loaiHeThong);
    
    void deleteByThongBao(ThongBao thongBao);
} 
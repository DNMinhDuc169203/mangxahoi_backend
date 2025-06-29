package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.CuocTroChuyen;
import com.mangxahoi.mangxahoi_backend.entity.ThongBaoTinNhan;
import com.mangxahoi.mangxahoi_backend.entity.TinNhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThongBaoTinNhanRepository extends JpaRepository<ThongBaoTinNhan, Integer> {
    List<ThongBaoTinNhan> findByCuocTroChuyen(CuocTroChuyen cuocTroChuyen);
    
    List<ThongBaoTinNhan> findByTinNhan(TinNhan tinNhan);
    
    void deleteByCuocTroChuyen(CuocTroChuyen cuocTroChuyen);
    
    void deleteByTinNhan(TinNhan tinNhan);
} 
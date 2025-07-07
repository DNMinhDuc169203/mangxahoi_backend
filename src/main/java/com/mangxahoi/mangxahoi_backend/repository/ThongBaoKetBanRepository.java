package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.KetBan;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.ThongBaoKetBan;
import com.mangxahoi.mangxahoi_backend.enums.LoaiKetBanThongBao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThongBaoKetBanRepository extends JpaRepository<ThongBaoKetBan, Integer> {
    List<ThongBaoKetBan> findByNguoiGui(NguoiDung nguoiGui);
    
    List<ThongBaoKetBan> findByKetBan(KetBan ketBan);
    
    List<ThongBaoKetBan> findByLoaiKetBan(LoaiKetBanThongBao loaiKetBan);
    
    void deleteByNguoiGui(NguoiDung nguoiGui);
    
    void deleteByKetBan(KetBan ketBan);
    List<ThongBaoKetBan> findByKetBan_Id(Integer idKetBan);
} 
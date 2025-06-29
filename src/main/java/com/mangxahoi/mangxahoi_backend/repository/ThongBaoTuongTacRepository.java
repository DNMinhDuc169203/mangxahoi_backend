package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.BaiViet;
import com.mangxahoi.mangxahoi_backend.entity.BinhLuan;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.ThongBaoTuongTac;
import com.mangxahoi.mangxahoi_backend.enums.LoaiTuongTacThongBao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThongBaoTuongTacRepository extends JpaRepository<ThongBaoTuongTac, Integer> {
    List<ThongBaoTuongTac> findByNguoiGui(NguoiDung nguoiGui);
    
    List<ThongBaoTuongTac> findByBaiViet(BaiViet baiViet);
    
    List<ThongBaoTuongTac> findByBinhLuan(BinhLuan binhLuan);
    
    List<ThongBaoTuongTac> findByLoaiTuongTac(LoaiTuongTacThongBao loaiTuongTac);
    
    void deleteByNguoiGui(NguoiDung nguoiGui);
    
    void deleteByBaiViet(BaiViet baiViet);
    
    void deleteByBinhLuan(BinhLuan binhLuan);
} 
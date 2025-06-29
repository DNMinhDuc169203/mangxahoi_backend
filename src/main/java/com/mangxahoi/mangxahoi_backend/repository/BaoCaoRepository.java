package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.BaiViet;
import com.mangxahoi.mangxahoi_backend.entity.BaoCao;
import com.mangxahoi.mangxahoi_backend.entity.BinhLuan;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.enums.LoaiBaoCao;
import com.mangxahoi.mangxahoi_backend.enums.TrangThaiBaoCao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaoCaoRepository extends JpaRepository<BaoCao, Integer> {
    Page<BaoCao> findByTrangThai(TrangThaiBaoCao trangThai, Pageable pageable);
    
    List<BaoCao> findByNguoiBaoCao(NguoiDung nguoiBaoCao);
    
    List<BaoCao> findByNguoiDungBiBaoCao(NguoiDung nguoiDungBiBaoCao);
    
    List<BaoCao> findByBaiViet(BaiViet baiViet);
    
    List<BaoCao> findByBinhLuan(BinhLuan binhLuan);
    
    List<BaoCao> findByLyDo(LoaiBaoCao lyDo);
    
    long countByTrangThai(TrangThaiBaoCao trangThai);
    
    long countByNguoiDungBiBaoCao(NguoiDung nguoiDungBiBaoCao);
} 
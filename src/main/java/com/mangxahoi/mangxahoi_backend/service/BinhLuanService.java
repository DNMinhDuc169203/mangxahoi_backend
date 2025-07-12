package com.mangxahoi.mangxahoi_backend.service;

import com.mangxahoi.mangxahoi_backend.dto.BinhLuanDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BinhLuanService {
   
    BinhLuanDTO themBinhLuan(Integer idBaiViet, Integer idNguoiDung, Integer idBinhLuanCha, BinhLuanDTO binhLuanDTO);

    BinhLuanDTO capNhatBinhLuan(Integer id, Integer idNguoiDung, BinhLuanDTO binhLuanDTO);

    boolean xoaBinhLuan(Integer id, Integer idNguoiDung);
 
    Page<BinhLuanDTO> layBinhLuanGocTheoBaiViet(Integer idBaiViet, Integer idNguoiDungHienTai, Pageable pageable);
 
    Page<BinhLuanDTO> layBinhLuanPhanHoi(Integer idBinhLuanCha, Integer idNguoiDungHienTai, Pageable pageable);
    
    boolean thichBinhLuan(Integer idBinhLuan, Integer idNguoiDung);

    boolean boThichBinhLuan(Integer idBinhLuan, Integer idNguoiDung);
} 
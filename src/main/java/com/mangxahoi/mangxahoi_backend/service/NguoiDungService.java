package com.mangxahoi.mangxahoi_backend.service;

import com.mangxahoi.mangxahoi_backend.dto.request.DangNhapRequest;
import com.mangxahoi.mangxahoi_backend.dto.response.DangNhapResponse;
import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface NguoiDungService {
    NguoiDungDTO dangKy(NguoiDungDTO nguoiDungDTO, String matKhau);
    
    DangNhapResponse dangNhap(DangNhapRequest request);
    
    NguoiDungDTO capNhatThongTin(Integer id, NguoiDungDTO nguoiDungDTO);
    
    void xoaNguoiDung(Integer id);
    
    Optional<NguoiDungDTO> timTheoId(Integer id);
    
    Optional<NguoiDungDTO> timTheoEmail(String email);
    
    Optional<NguoiDungDTO> timTheoSoDienThoai(String soDienThoai);
    
    Page<NguoiDungDTO> timTatCa(Pageable pageable);
    
    Page<NguoiDungDTO> timTheoHoTen(String hoTen, Pageable pageable);
    
    boolean xacThucEmail(String email, String token);
    
    boolean datLaiMatKhau(String email, String matKhauMoi);
} 
package com.mangxahoi.mangxahoi_backend.service;

import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import com.mangxahoi.mangxahoi_backend.dto.LoiMoiKetBanDaGuiDTO;
import com.mangxahoi.mangxahoi_backend.dto.LoiMoiKetBanDaNhanDTO;
import com.mangxahoi.mangxahoi_backend.dto.BanBeChungDTO;
import com.mangxahoi.mangxahoi_backend.enums.TrangThaiKetBan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface KetBanService {
    Integer guiLoiMoiKetBan(Integer idNguoiGui, Integer idNguoiNhan);
    
    boolean chapNhanLoiMoiKetBan(Integer idNguoiDung, Integer idLoiMoi);
    
    boolean tuChoiLoiMoiKetBan(Integer idNguoiDung, Integer idLoiMoi);
    
    boolean huyLoiMoiKetBan(Integer idNguoiDung, Integer idLoiMoi);
    
    boolean huyKetBan(Integer idNguoiDung1, Integer idNguoiDung2);
    
    boolean chanNguoiDung(Integer idNguoiDung, Integer idNguoiDungBiChan);
    
    boolean boChanNguoiDung(Integer idNguoiDung, Integer idNguoiDungBiChan);
    
    Page<NguoiDungDTO> danhSachBanBe(Integer idNguoiDung, Pageable pageable);
    
    Page<LoiMoiKetBanDaNhanDTO> danhSachLoiMoiKetBan(Integer idNguoiDung, Pageable pageable);
    
    Page<LoiMoiKetBanDaGuiDTO> danhSachLoiMoiDaGui(Integer idNguoiDung, Pageable pageable);
    
    Page<NguoiDungDTO> danhSachNguoiDungBiChan(Integer idNguoiDung, Pageable pageable);
    
    Page<NguoiDungDTO> goiYKetBan(Integer idNguoiDung, Pageable pageable);
    
    TrangThaiKetBan kiemTraTrangThaiKetBan(Integer idNguoiDung1, Integer idNguoiDung2);
    
    long demSoBanBe(Integer idNguoiDung);
    
    long demSoLoiMoiKetBan(Integer idNguoiDung);
    
    // Thêm method mới cho bạn bè chung
    List<NguoiDungDTO> timBanBeChung(Integer idNguoiDung1, Integer idNguoiDung2);
    
    long demSoBanBeChung(Integer idNguoiDung1, Integer idNguoiDung2);
    
    BanBeChungDTO layThongTinBanBeChung(Integer idNguoiDung1, Integer idNguoiDung2);
} 
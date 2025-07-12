package com.mangxahoi.mangxahoi_backend.service;

import com.mangxahoi.mangxahoi_backend.dto.request.DangNhapRequest;
import com.mangxahoi.mangxahoi_backend.dto.response.DangNhapResponse;
import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import com.mangxahoi.mangxahoi_backend.dto.request.PrivacySettingsRequest;
import com.mangxahoi.mangxahoi_backend.dto.response.LichSuGoiYDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface NguoiDungService {
    NguoiDungDTO dangKy(NguoiDungDTO nguoiDungDTO, String matKhau);
    
    DangNhapResponse dangNhap(DangNhapRequest request);
    
    NguoiDungDTO capNhatThongTin(Integer id, NguoiDungDTO nguoiDungDTO);
    
    void xoaNguoiDung(Integer id);
    
    Optional<NguoiDungDTO> timTheoId(Integer profileId, String requesterToken);
    
    Optional<NguoiDungDTO> timTheoEmail(String email);
    
    Optional<NguoiDungDTO> timTheoSoDienThoai(String soDienThoai);
    
    Page<NguoiDungDTO> timTatCa(Pageable pageable);
    
    Page<NguoiDungDTO> timTheoHoTen(String hoTen, Pageable pageable);
    
    boolean xacThucEmail(String email, String token);
    
    boolean datLaiMatKhau(String email, String matKhauMoi);

    NguoiDungDTO layThongTinHienTai(String token);
   
    String uploadAnhDaiDien(Integer id, MultipartFile file, boolean laAnhChinh) throws IOException;

    boolean doiMatKhau(String token, String matKhauCu, String matKhauMoi);

    void xoaAnhDaiDien(Integer nguoiDungId, Integer anhId) throws IOException;

    NguoiDungDTO thayDoiMucRiengTu(String token, com.mangxahoi.mangxahoi_backend.enums.CheDoBaiViet cheDoMoi);

    NguoiDungDTO capNhatCaiDatRiengTu(String token, PrivacySettingsRequest request);

    Page<NguoiDungDTO> timTheoSoDienThoaiGanDung(String soDienThoai, Pageable pageable);

    List<LichSuGoiYDTO> layGoiYKetBan(String token);
} 
// src/main/java/com/mangxahoi/mangxahoi_backend/admin/service/AdminService.java

package com.mangxahoi.mangxahoi_backend.admin.service;

import com.mangxahoi.mangxahoi_backend.admin.dto.request.DangNhapAdminRequest;
import com.mangxahoi.mangxahoi_backend.admin.dto.response.ThongKeResponse;
import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import com.mangxahoi.mangxahoi_backend.dto.response.DangNhapResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.mangxahoi.mangxahoi_backend.admin.dto.request.ThemViPhamRequest;
import com.mangxahoi.mangxahoi_backend.admin.dto.response.LichSuViPhamDTO;
import com.mangxahoi.mangxahoi_backend.admin.dto.response.ThongTinViPhamNguoiDungDTO;
import java.util.List;
import com.mangxahoi.mangxahoi_backend.entity.BaoCao;
import com.mangxahoi.mangxahoi_backend.admin.dto.response.BaoCaoDTO;

public interface AdminService {
    
    DangNhapResponse dangNhap(DangNhapAdminRequest request);
    
    ThongKeResponse layThongKeTongQuat();
    
    Page<NguoiDungDTO> danhSachNguoiDung(Pageable pageable);
    
    void khoaTaiKhoan(Integer id, String lyDo);
    
    void moKhoaTaiKhoan(Integer id);
    
    Page<BaoCaoDTO> danhSachBaoCao(String trangThai, org.springframework.data.domain.Pageable pageable);
    
    void xuLyBaoCao(Integer id, String trangThai, String ghiChu);

    // Lấy lịch sử vi phạm của user
    List<LichSuViPhamDTO> lichSuViPhamNguoiDung(Integer userId);

    // Thêm mới vi phạm cho user (tự động xác định hình phạt)
    LichSuViPhamDTO themViPhamNguoiDung(ThemViPhamRequest request, Integer adminId);

    // Lấy tổng quan vi phạm của user
    ThongTinViPhamNguoiDungDTO thongTinViPhamNguoiDung(Integer userId);

    List<BaoCaoDTO> findTop5BaoCaoMoiNhat();

    void logout(String token);
}
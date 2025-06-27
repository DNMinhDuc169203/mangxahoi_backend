// src/main/java/com/mangxahoi/mangxahoi_backend/admin/service/AdminService.java

package com.mangxahoi.mangxahoi_backend.admin.service;

import com.mangxahoi.mangxahoi_backend.admin.dto.request.DangNhapAdminRequest;
import com.mangxahoi.mangxahoi_backend.admin.dto.response.ThongKeResponse;
import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import com.mangxahoi.mangxahoi_backend.dto.response.DangNhapResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    
    DangNhapResponse dangNhap(DangNhapAdminRequest request);
    
    ThongKeResponse layThongKeTongQuat();
    
    Page<NguoiDungDTO> danhSachNguoiDung(Pageable pageable);
    
    void khoaTaiKhoan(Integer id, String lyDo);
    
    void moKhoaTaiKhoan(Integer id);
    
    Page<Object> danhSachBaoCao(String trangThai, Pageable pageable);
    
    void xuLyBaoCao(Integer id, String trangThai, String ghiChu);
}
package com.mangxahoi.mangxahoi_backend.admin.service.impl;

import com.mangxahoi.mangxahoi_backend.admin.dto.request.DangNhapAdminRequest;
import com.mangxahoi.mangxahoi_backend.admin.dto.response.ThongKeResponse;
import com.mangxahoi.mangxahoi_backend.admin.service.AdminService;
import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import com.mangxahoi.mangxahoi_backend.dto.response.DangNhapResponse;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDungAnh;
import com.mangxahoi.mangxahoi_backend.entity.PhienDangNhapNguoiDung;
import com.mangxahoi.mangxahoi_backend.enums.VaiTro;
import com.mangxahoi.mangxahoi_backend.exception.AuthException;
import com.mangxahoi.mangxahoi_backend.exception.ResourceNotFoundException;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungAnhRepository;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import com.mangxahoi.mangxahoi_backend.repository.PhienDangNhapNguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final NguoiDungRepository nguoiDungRepository;
    private final NguoiDungAnhRepository nguoiDungAnhRepository;
    private final PhienDangNhapNguoiDungRepository phienDangNhapRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public DangNhapResponse dangNhap(DangNhapAdminRequest request) {
        // Tìm người dùng theo email
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("Email không tồn tại", AuthException.EMAIL_SDT_KHONG_TON_TAI));

        // Kiểm tra mật khẩu
        boolean matKhauHopLe = false;
        
        // Kiểm tra nếu mật khẩu đã được mã hóa bằng BCrypt
        if (nguoiDung.getMatKhauHash() != null && nguoiDung.getMatKhauHash().startsWith("$2a$")) {
            matKhauHopLe = passwordEncoder.matches(request.getMatKhau(), nguoiDung.getMatKhauHash());
        } else {
            // Nếu mật khẩu chưa được mã hóa, so sánh trực tiếp
            matKhauHopLe = request.getMatKhau().equals(nguoiDung.getMatKhauHash());
        }
        
        if (!matKhauHopLe) {
            throw new AuthException("Mật khẩu không đúng", AuthException.MAT_KHAU_KHONG_DUNG);
        }

        // Kiểm tra vai trò - phải là admin
        if (nguoiDung.getVaiTro() != VaiTro.quan_tri_vien) {
            throw new AuthException("Tài khoản không có quyền quản trị", "NOT_ADMIN");
        }

        // Cập nhật thời gian đăng nhập
        nguoiDung.setLanDangNhapCuoi(LocalDateTime.now());
        nguoiDungRepository.save(nguoiDung);

        // Tạo phiên đăng nhập mới
        String token = UUID.randomUUID().toString();
        LocalDateTime hetHan = LocalDateTime.now().plusDays(1); // Phiên admin ngắn hơn phiên thường

        PhienDangNhapNguoiDung phien = PhienDangNhapNguoiDung.builder()
                .nguoiDung(nguoiDung)
                .maPhien(token)
                .hetHanLuc(hetHan)
                .vaiTro(VaiTro.quan_tri_vien)
                .build();

        phienDangNhapRepository.save(phien);

        // Trả về thông tin đăng nhập
        NguoiDungDTO nguoiDungDTO = chuyenSangDTO(nguoiDung);
        
        return DangNhapResponse.builder()
                .token(token)
                .nguoiDung(nguoiDungDTO)
                .build();
    }

    @Override
    public ThongKeResponse layThongKeTongQuat() {
        // Đây sẽ là logic để lấy các thống kê từ cơ sở dữ liệu
        // Giả lập dữ liệu
        return ThongKeResponse.builder()
                .tongSoNguoiDung(nguoiDungRepository.count())
                .nguoiDungMoi(0)
                .tongSoBaiViet(0) 
                .baiVietMoi(0)
                .tongSoBinhLuan(0)
                .binhLuanMoi(0)
                .tongSoBaoCao(0)
                .baoCaoChuaXuLy(0)
                .nguoiDungBiKhoa(0)
                .trendTuanNay(new ThongKeResponse.ThongKeTrend("Chưa có dữ liệu", 0, 0, 0))
                .build();
    }

    @Override
    public Page<NguoiDungDTO> danhSachNguoiDung(Pageable pageable) {
        return nguoiDungRepository.findAll(pageable)
                .map(this::chuyenSangDTO);
    }

    @Override
    @Transactional
    public void khoaTaiKhoan(Integer id, String lyDo) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", id));
                
        // Không cho khóa tài khoản admin
        if (nguoiDung.getVaiTro() == VaiTro.quan_tri_vien) {
            throw new AuthException("Không thể khóa tài khoản quản trị viên", "ADMIN_CANNOT_BE_LOCKED");
        }

        nguoiDung.setBiTamKhoa(true);
        nguoiDung.setLyDoTamKhoa(lyDo);
        nguoiDung.setNgayTamKhoa(LocalDateTime.now());
        nguoiDungRepository.save(nguoiDung);
        
        // Đăng xuất người dùng bằng cách xóa tất cả phiên đăng nhập
        phienDangNhapRepository.deleteByNguoiDung(nguoiDung);
    }

    @Override
    @Transactional
    public void moKhoaTaiKhoan(Integer id) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", id));

        nguoiDung.setBiTamKhoa(false);
        nguoiDung.setLyDoTamKhoa(null);
        nguoiDung.setNgayMoKhoa(LocalDateTime.now());
        nguoiDungRepository.save(nguoiDung);
    }

    @Override
    public Page<Object> danhSachBaoCao(String trangThai, Pageable pageable) {
        // Giả lập trả về dữ liệu rỗng
        return Page.empty();
    }

    @Override
    public void xuLyBaoCao(Integer id, String trangThai, String ghiChu) {
        // Giả lập xử lý báo cáo
    }
    
    private NguoiDungDTO chuyenSangDTO(NguoiDung nguoiDung) {
        // Lấy ảnh đại diện
        String anhDaiDien = null;
        Optional<NguoiDungAnh> anhDaiDienOpt = nguoiDungAnhRepository.findByNguoiDungAndLaAnhChinh(nguoiDung, true);
        if (anhDaiDienOpt.isPresent()) {
            anhDaiDien = anhDaiDienOpt.get().getUrl();
        }
        
        return NguoiDungDTO.builder()
                .id(nguoiDung.getId())
                .email(nguoiDung.getEmail())
                .soDienThoai(nguoiDung.getSoDienThoai())
                .hoTen(nguoiDung.getHoTen())
                .tieuSu(nguoiDung.getTieuSu())
                .ngaySinh(nguoiDung.getNgaySinh())
                .gioiTinh(nguoiDung.getGioiTinh())
                .diaChi(nguoiDung.getDiaChi())
                .daXacThuc(nguoiDung.getDaXacThuc())
                .dangHoatDong(nguoiDung.getDangHoatDong())
                .mucRiengTu(nguoiDung.getMucRiengTu())
                .ngayTao(nguoiDung.getNgayTao())
                .lanDangNhapCuoi(nguoiDung.getLanDangNhapCuoi())
                .soBanBe(nguoiDung.getSoBanBe())
                .soBaiDang(nguoiDung.getSoBaiDang())
                .vaiTro(nguoiDung.getVaiTro())
                .anhDaiDien(anhDaiDien)
                .build();
    }
}
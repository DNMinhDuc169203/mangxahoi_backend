package com.mangxahoi.mangxahoi_backend.service;

import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.enums.LoaiMaXacThuc;
import com.mangxahoi.mangxahoi_backend.exception.AuthException;
import com.mangxahoi.mangxahoi_backend.exception.ResourceNotFoundException;
import com.mangxahoi.mangxahoi_backend.exception.ValidationException;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class QuenMatKhauService {

    private final NguoiDungRepository nguoiDungRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Tạo và gửi mã xác thực qua email/SMS
     */
    @Transactional
    public void taoVaGuiMaXacThuc(String emailHoacSoDienThoai) {
        // Kiểm tra người dùng có tồn tại không
        NguoiDung nguoiDung = timNguoiDungTheoEmailHoacSdt(emailHoacSoDienThoai);
        
        // Tạo mã xác thực ngẫu nhiên 6 số
        String ma = taoMaNgauNhien();
        
        // Lưu mã xác thực vào thông tin người dùng
        nguoiDung.setTokenXacThuc(ma);
        nguoiDung.setLoaiMaXacThuc(LoaiMaXacThuc.quen_mat_khau);
        nguoiDung.setNgayCapNhat(LocalDateTime.now());
        
        nguoiDungRepository.save(nguoiDung);
        
        // Gửi email chứa mã xác thực
        if (emailHoacSoDienThoai.contains("@")) {
            emailService.guiMaXacThuc(emailHoacSoDienThoai, ma, "QUEN_MAT_KHAU");
        } else {
            // Gửi SMS nếu là số điện thoại (chưa triển khai)
            // smsService.guiMaXacThuc(emailHoacSoDienThoai, ma);
        }
    }
    
    /**
     * Xác thực mã và đặt lại mật khẩu mới
     */
    @Transactional
    public boolean xacThucVaDoiMatKhau(String emailHoacSoDienThoai, String ma, String matKhauMoi) {
        // Tìm người dùng
        NguoiDung nguoiDung = timNguoiDungTheoEmailHoacSdt(emailHoacSoDienThoai);
        
        // Kiểm tra mã xác thực
        if (nguoiDung.getTokenXacThuc() == null || !nguoiDung.getTokenXacThuc().equals(ma)) {
            throw new AuthException("Mã xác thực không đúng", "AUTH_009");
        }
        
        if (nguoiDung.getLoaiMaXacThuc() != LoaiMaXacThuc.quen_mat_khau) {
            throw new AuthException("Mã xác thực không phải cho mục đích đặt lại mật khẩu", "AUTH_010");
        }
        
        // Cập nhật mật khẩu mới
        nguoiDung.setMatKhauHash(passwordEncoder.encode(matKhauMoi));
        
        // Xóa mã xác thực
        nguoiDung.setTokenXacThuc(null);
        nguoiDung.setLoaiMaXacThuc(null);
        
        nguoiDungRepository.save(nguoiDung);
        
        return true;
    }
    
    /**
     * Tìm người dùng theo email hoặc số điện thoại
     */
    private NguoiDung timNguoiDungTheoEmailHoacSdt(String emailHoacSoDienThoai) {
        if (emailHoacSoDienThoai == null || emailHoacSoDienThoai.trim().isEmpty()) {
            throw new ValidationException("Email hoặc số điện thoại không được để trống");
        }
        
        return nguoiDungRepository.findByEmail(emailHoacSoDienThoai)
                .orElseGet(() -> nguoiDungRepository.findBySoDienThoai(emailHoacSoDienThoai)
                        .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "email hoặc số điện thoại", emailHoacSoDienThoai)));
    }
    
    /**
     * Tạo mã xác thực ngẫu nhiên 6 số
     */
    private String taoMaNgauNhien() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000); // số từ 100000 đến 999999
        return String.valueOf(number);
    }
} 
package com.mangxahoi.mangxahoi_backend.config;

import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.enums.VaiTro;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class InitAdminConfig {
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {
            String email = "admin@admin.com";
            if (nguoiDungRepository.findByEmail(email).isEmpty()) {
                NguoiDung admin = new NguoiDung();
                admin.setEmail(email);
                admin.setHoTen("Admin");
                admin.setMatKhauHash(passwordEncoder.encode("admin123"));
                admin.setVaiTro(VaiTro.quan_tri_vien);
                admin.setDaXacThuc(true);
                admin.setSoDienThoai("0999999999");
                // Set các trường cần thiết khác nếu có
                nguoiDungRepository.save(admin);
                System.out.println("Đã tạo tài khoản admin mặc định: " + email + " / admin123");
            }
        };
    }
} 
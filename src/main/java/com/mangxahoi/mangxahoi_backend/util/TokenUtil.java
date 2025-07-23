package com.mangxahoi.mangxahoi_backend.util;

import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.PhienDangNhapNguoiDung;
import com.mangxahoi.mangxahoi_backend.exception.AuthException;
import com.mangxahoi.mangxahoi_backend.repository.PhienDangNhapNguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TokenUtil {

    private final PhienDangNhapNguoiDungRepository phienDangNhapRepository;

    /**
     * Lấy thông tin người dùng từ token
     * 
     * @param token Token xác thực
     * @return Thông tin người dùng 
     * @throws AuthException Nếu token không hợp lệ hoặc đã hết hạn
     */
    public NguoiDung layNguoiDungTuToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new AuthException("Token không được để trống", AuthException.TOKEN_KHONG_HOP_LE);
        }

        // Tìm phiên đăng nhập theo token
        PhienDangNhapNguoiDung phien = phienDangNhapRepository.findByMaPhien(token)
                .orElseThrow(() -> new AuthException("Token không hợp lệ", AuthException.TOKEN_KHONG_HOP_LE));

        // Kiểm tra token đã hết hạn chưa
        if (phien.getHetHanLuc() != null && phien.getHetHanLuc().isBefore(LocalDateTime.now())) {
            throw new AuthException("Token đã hết hạn", AuthException.TOKEN_KHONG_HOP_LE);
        }

        return phien.getNguoiDung();
    }

    /**
     * Kiểm tra token có hợp lệ không
     * 
     * @param token Token cần kiểm tra
     * @return true nếu token hợp lệ, false nếu không
     */
    public boolean kiemTraTokenHopLe(String token) {
        try {
            layNguoiDungTuToken(token);
            return true;
        } catch (AuthException e) {
            return false;
        }
    }
} 
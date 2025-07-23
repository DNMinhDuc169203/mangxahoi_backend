package com.mangxahoi.mangxahoi_backend.config;

import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import com.mangxahoi.mangxahoi_backend.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class AutoUnlockAccountScheduler {
    private final NguoiDungRepository nguoiDungRepository;
    private final ThongBaoService thongBaoService;

    /**Chạy mỗi 5 phút: tự động mở khóa tài khoản nếu đã hết hạn khóa*/
    @Scheduled(fixedRate = 5 * 60 * 1000) // 5 phút
    public void autoUnlockAccounts() {
        List<NguoiDung> lockedUsers = nguoiDungRepository.findAllByBiTamKhoaTrueAndNgayMoKhoaIsNotNull();
        LocalDateTime now = LocalDateTime.now();
        for (NguoiDung user : lockedUsers) {
            if (user.getNgayMoKhoa() != null && !now.isBefore(user.getNgayMoKhoa())) {
                user.setBiTamKhoa(false);
                user.setLyDoTamKhoa(null);
                user.setNgayMoKhoa(null);
                nguoiDungRepository.save(user);
                // Gửi thông báo hệ thống
                try {
                    thongBaoService.guiThongBaoHeThong(
                        user.getId(),
                        "Mở khóa tài khoản",
                        "Tài khoản của bạn đã được tự động mở khóa sau khi hết thời hạn tạm khóa. Bạn có thể đăng nhập và sử dụng lại bình thường."
                    );
                } catch (Exception e) {
                    System.err.println("Lỗi gửi thông báo mở khóa tự động: " + e.getMessage());
                }
            }
        }
    }
} 
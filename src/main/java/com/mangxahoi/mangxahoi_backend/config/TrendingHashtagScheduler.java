package com.mangxahoi.mangxahoi_backend.config;

import com.mangxahoi.mangxahoi_backend.entity.Hashtag;
import com.mangxahoi.mangxahoi_backend.repository.HashtagRepository;
import com.mangxahoi.mangxahoi_backend.repository.BaiVietHashtagRepository;
import com.mangxahoi.mangxahoi_backend.entity.BaiVietHashtag;
import com.mangxahoi.mangxahoi_backend.service.ThongBaoService;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class TrendingHashtagScheduler {
    private final HashtagRepository hashtagRepository;
    private final BaiVietHashtagRepository baiVietHashtagRepository;
    private final ThongBaoService thongBaoService;
    private final NguoiDungRepository nguoiDungRepository;

    // Chạy mỗi 2 phút (test nhanh)
    @Scheduled(cron = "0 */2 * * * *")
    @Transactional
    public void updateTrendingHashtags() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusHours(24);
        // Đếm số lần sử dụng hashtag trong 24h qua
        List<BaiVietHashtag> recentLinks = baiVietHashtagRepository.findByCreatedAtBetween(from, now);
        Map<Integer, Integer> hashtagCount = new HashMap<>();
        for (BaiVietHashtag link : recentLinks) {
            Integer id = link.getHashtag().getId();
            hashtagCount.put(id, hashtagCount.getOrDefault(id, 0) + 1);
        }
        // Lấy top 3 hashtag nhiều nhất
        List<Map.Entry<Integer, Integer>> sorted = new ArrayList<>(hashtagCount.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());
        Set<Integer> trendingIds = new HashSet<>();
        for (int i = 0; i < Math.min(3, sorted.size()); i++) {
            trendingIds.add(sorted.get(i).getKey());
        }
        // Cập nhật trạng thái trending
        List<Hashtag> all = hashtagRepository.findAll();
        for (Hashtag h : all) {
            h.setDangXuHuong(trendingIds.contains(h.getId()));
        }
        hashtagRepository.saveAll(all);
    }

    // Tự động hết hạn ưu tiên hashtag (mỗi 2 phút)
    @Scheduled(cron = "0 */2 * * * *")
    @Transactional
    public void autoUnpromoteExpiredHashtags() {
        LocalDateTime now = LocalDateTime.now();
        List<Hashtag> uuTienList = hashtagRepository.findAll();
        for (Hashtag h : uuTienList) {
            if (Boolean.TRUE.equals(h.getUuTien()) && h.getThoiGianUuTienKetThuc() != null && h.getThoiGianUuTienKetThuc().isBefore(now)) {
                h.setUuTien(false);
                h.setThoiGianUuTienBatDau(null);
                h.setThoiGianUuTienKetThuc(null);
                h.setMoTaUuTien(null);
            }
        }
        hashtagRepository.saveAll(uuTienList);
    }

    // Tự động ưu tiên top 3 hashtag được sử dụng nhiều nhất trong 5 phút gần nhất
    @Scheduled(cron = "0 */2 * * * *")
    @Transactional
    public void autoPromoteTopRecentHashtags() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusDays(1);
        List<BaiVietHashtag> recentLinks = baiVietHashtagRepository.findByCreatedAtBetween(from, now);
        Map<Integer, Integer> hashtagCount = new HashMap<>();
        for (BaiVietHashtag link : recentLinks) {
            Integer id = link.getHashtag().getId();
            hashtagCount.put(id, hashtagCount.getOrDefault(id, 0) + 1);
        }
        // Lấy top 3 hashtag nhiều nhất
        List<Map.Entry<Integer, Integer>> sorted = new ArrayList<>(hashtagCount.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());
        Set<Integer> topIds = new HashSet<>();
        for (int i = 0; i < Math.min(3, sorted.size()); i++) {
            topIds.add(sorted.get(i).getKey());
        }
        List<Hashtag> all = hashtagRepository.findAll();
        for (Hashtag h : all) {
            boolean wasPromoted = Boolean.TRUE.equals(h.getUuTien());
            if (topIds.contains(h.getId())) {
                if (!wasPromoted) {
                    // 1. Lấy user từng dùng hashtag này
                    List<BaiVietHashtag> links = baiVietHashtagRepository.findByHashtag(h);
                    Set<NguoiDung> usedUsers = links.stream()
                        .map(link -> link.getBaiViet().getNguoiDung())
                        .collect(Collectors.toSet());
                    // 2. Gửi thông báo cho user từng dùng hashtag
                    for (NguoiDung user : usedUsers) {
                        thongBaoService.guiThongBaoHeThong(
                            user.getId(),
                            "Chúc mừng! Hashtag " + h.getTen() + " của bạn đã lên xu hướng!",
                            "Hashtag " + h.getTen() + " mà bạn từng sử dụng vừa lên xu hướng. Hãy tiếp tục chia sẻ nội dung chất lượng!"
                        );
                    }
                    // 3. Gửi thông báo cho các user còn lại
                    List<NguoiDung> allUsers = nguoiDungRepository.findAll();
                    for (NguoiDung user : allUsers) {
                        if (!usedUsers.contains(user)) {
                            thongBaoService.guiThongBaoHeThong(
                                user.getId(),
                                "Hashtag " + h.getTen() + " đang lên xu hướng!",
                                "Hãy sử dụng hashtag " + h.getTen() + " để bài viết của bạn dễ tiếp cận hơn!"
                            );
                        }
                    }
                }
                h.setUuTien(true);
                h.setThoiGianUuTienBatDau(now);
                h.setThoiGianUuTienKetThuc(now.plusDays(1));
                h.setMoTaUuTien("Tự động ưu tiên top 3 hashtag sử dụng nhiều nhất trong 1 ngày gần nhất");
            } else if (h.getMoTaUuTien() != null && h.getMoTaUuTien().contains("top 3")) {
                h.setUuTien(false);
                h.setThoiGianUuTienBatDau(null);
                h.setThoiGianUuTienKetThuc(null);
                h.setMoTaUuTien(null);
            }
        }
        hashtagRepository.saveAll(all);
    }
} 
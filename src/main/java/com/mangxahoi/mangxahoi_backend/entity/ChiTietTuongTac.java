package com.mangxahoi.mangxahoi_backend.entity;

import com.mangxahoi.mangxahoi_backend.enums.LoaiTuongTac;
import com.mangxahoi.mangxahoi_backend.enums.NguonTuongTac;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "chi_tiet_tuong_tac")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietTuongTac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_1", nullable = false)
    private NguoiDung nguoi1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_2", nullable = false)
    private NguoiDung nguoi2;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_tuong_tac", nullable = false)
    private LoaiTuongTac loaiTuongTac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bai_viet")
    private BaiViet baiViet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_binh_luan")
    private BinhLuan binhLuan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tin_nhan")
    private TinNhan tinNhan;

    @Column(name = "diem_tuong_tac")
    private Integer diemTuongTac;

    @Column(name = "trong_so", precision = 3, scale = 2)
    private BigDecimal trongSo;

    @Column(name = "thoi_gian_tuong_tac")
    private Integer thoiGianTuongTac;

    @Enumerated(EnumType.STRING)
    @Column(name = "nguon_tuong_tac")
    private NguonTuongTac nguonTuongTac;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        diemTuongTac = 0;
        trongSo = new BigDecimal("1.00");
        thoiGianTuongTac = 0;
        nguonTuongTac = NguonTuongTac.news_feed;
    }
} 
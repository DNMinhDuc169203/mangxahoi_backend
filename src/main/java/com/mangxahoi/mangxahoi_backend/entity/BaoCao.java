package com.mangxahoi.mangxahoi_backend.entity;

import com.mangxahoi.mangxahoi_backend.enums.LoaiBaoCao;
import com.mangxahoi.mangxahoi_backend.enums.TrangThaiBaoCao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bao_cao")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaoCao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_bao_cao", nullable = false)
    private NguoiDung nguoiBaoCao;

    @Enumerated(EnumType.STRING)
    @Column(name = "ly_do", nullable = false)
    private LoaiBaoCao lyDo;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiBaoCao trangThai;

    @Column(name = "ghi_chu_xu_ly", columnDefinition = "TEXT")
    private String ghiChuXuLy;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_xu_ly")
    private LocalDateTime ngayXuLy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bai_viet")
    private BaiViet baiViet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_binh_luan")
    private BinhLuan binhLuan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung_bi_bao_cao")
    private NguoiDung nguoiDungBiBaoCao;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        trangThai = TrangThaiBaoCao.cho_xu_ly;
    }
} 
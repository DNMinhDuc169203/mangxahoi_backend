package com.mangxahoi.mangxahoi_backend.entity;

import com.mangxahoi.mangxahoi_backend.enums.VaiTro;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "phien_dang_nhap_nguoi_dung")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhienDangNhapNguoiDung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "ma_phien", nullable = false, unique = true)
    private String maPhien;

    @Column(name = "thong_tin_thiet_bi")
    private String thongTinThietBi;

    @Column(name = "dia_chi_ip")
    private String diaChiIp;

    @Column(name = "het_han_luc", nullable = false)
    private LocalDateTime hetHanLuc;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Enumerated(EnumType.STRING)
    private VaiTro vaiTro;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
    }
} 
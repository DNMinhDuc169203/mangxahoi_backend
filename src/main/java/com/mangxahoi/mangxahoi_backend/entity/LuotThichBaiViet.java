package com.mangxahoi.mangxahoi_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "luot_thich_bai_viet")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LuotThichBaiViet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bai_viet", nullable = false)
    private BaiViet baiViet;

    @Column(name = "trang_thai_thich")
    private Boolean trangThaiThich;

    @Column(name = "thong_tin_thiet_bi")
    private String thongTinThietBi;

    @Column(name = "dia_chi_ip", length = 45)
    private String diaChiIp;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_huy_thich")
    private LocalDateTime ngayHuyThich;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        trangThaiThich = true;
    }
} 
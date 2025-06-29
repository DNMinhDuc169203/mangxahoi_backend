package com.mangxahoi.mangxahoi_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "tuong_tac_profile")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TuongTacProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_xem", nullable = false)
    private NguoiDung nguoiXem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profile_duoc_xem", nullable = false)
    private NguoiDung profileDuocXem;

    @Column(name = "loai_tuong_tac")
    private String loaiTuongTac;

    @Column(name = "thoi_gian_xem")
    private Integer thoiGianXem;

    @Column(name = "so_bai_viet_xem")
    private Integer soBaiVietXem;

    @Column(name = "so_anh_xem")
    private Integer soAnhXem;

    @Column(name = "cuon_trang")
    private Integer cuonTrang;

    @Column(name = "nguon_truy_cap")
    private String nguonTruyCap;

    @Column(name = "thong_tin_them", columnDefinition = "json")
    private String thongTinThem;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        loaiTuongTac = "xem_profile";
        thoiGianXem = 0;
        soBaiVietXem = 0;
        soAnhXem = 0;
        cuonTrang = 0;
    }
} 
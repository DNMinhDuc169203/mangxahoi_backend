package com.mangxahoi.mangxahoi_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "phan_tich_hanh_vi")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhanTichHanhVi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "ngay_phan_tich", nullable = false)
    private LocalDate ngayPhanTich;

    @Column(name = "so_bai_viet_dang")
    private Integer soBaiVietDang;

    @Column(name = "so_binh_luan")
    private Integer soBinhLuan;

    @Column(name = "so_like_gui")
    private Integer soLikeGui;

    @Column(name = "so_like_nhan")
    private Integer soLikeNhan;

    @Column(name = "so_profile_xem")
    private Integer soProfileXem;

    @Column(name = "so_tin_nhan_gui")
    private Integer soTinNhanGui;

    @Column(name = "thoi_gian_online")
    private Integer thoiGianOnline;

    @Column(name = "hashtag_yeu_thich", columnDefinition = "json")
    private String hashtagYeuThich;

    @Column(name = "thoi_gian_hoat_dong_cao", columnDefinition = "json")
    private String thoiGianHoatDongCao;

    @Column(name = "muc_do_tuong_tac")
    private String mucDoTuongTac;

    @Column(name = "diem_hoat_dong")
    private Integer diemHoatDong;

    @Column(name = "diem_tuong_tac_xa_hoi")
    private Integer diemTuongTacXaHoi;

    @Column(name = "muc_do_pho_bien")
    private String mucDoPhoVien;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void onCreate() {
        soBaiVietDang = 0;
        soBinhLuan = 0;
        soLikeGui = 0;
        soLikeNhan = 0;
        soProfileXem = 0;
        soTinNhanGui = 0;
        thoiGianOnline = 0;
        mucDoTuongTac = "trung_binh";
        diemHoatDong = 0;
        diemTuongTacXaHoi = 0;
        mucDoPhoVien = "trung_binh";
        ngayCapNhat = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
} 
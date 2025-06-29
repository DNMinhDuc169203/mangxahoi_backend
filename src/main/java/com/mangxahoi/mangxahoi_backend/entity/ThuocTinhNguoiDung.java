package com.mangxahoi.mangxahoi_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "thuoc_tinh_nguoi_dung")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThuocTinhNguoiDung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "so_thich", columnDefinition = "json")
    private String soThich;

    @Column(name = "dia_diem_yeu_thich", columnDefinition = "json")
    private String diaDiemYeuThich;

    @Column(name = "thoi_gian_hoat_dong", columnDefinition = "json")
    private String thoiGianHoatDong;

    @Column(name = "nhom_tuoi")
    private String nhomTuoi;

    @Column(name = "nghe_nghiep", length = 100)
    private String ngheNghiep;

    @Column(name = "tinh_thanh", length = 100)
    private String tinhThanh;

    @Column(name = "quan_huyen", length = 100)
    private String quanHuyen;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
} 
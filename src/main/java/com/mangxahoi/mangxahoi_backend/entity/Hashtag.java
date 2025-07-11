package com.mangxahoi.mangxahoi_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hashtag")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String ten;

    @Column(name = "so_lan_su_dung")
    private Integer soLanSuDung;

    @Column(name = "dang_xu_huong")
    private Boolean dangXuHuong;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "uu_tien")
    private Boolean uuTien;

    @Column(name = "thoi_gian_uu_tien_bat_dau")
    private LocalDateTime thoiGianUuTienBatDau;

    @Column(name = "thoi_gian_uu_tien_ket_thuc")
    private LocalDateTime thoiGianUuTienKetThuc;

    @Column(name = "mo_ta_uu_tien", columnDefinition = "TEXT")
    private String moTaUuTien;

    @ManyToMany(mappedBy = "hashtags")
    private List<BaiViet> baiViets;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        soLanSuDung = 0;
        dangXuHuong = false;
        uuTien = false;
    }
} 
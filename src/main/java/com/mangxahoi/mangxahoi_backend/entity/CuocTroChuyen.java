package com.mangxahoi.mangxahoi_backend.entity;

import com.mangxahoi.mangxahoi_backend.enums.LoaiCuocTroChuyen;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cuoc_tro_chuyen")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuocTroChuyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private LoaiCuocTroChuyen loai;

    @Column(name = "ten_nhom", length = 100)
    private String tenNhom;

    @Column(name = "anh_nhom")
    private String anhNhom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_tao")
    private NguoiDung nguoiTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_tham_gia")
    private NguoiDung nguoiThamGia;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "so_thanh_vien")
    private Integer soThanhVien;

    @Column(name = "tin_nhan_cuoi")
    private LocalDateTime tinNhanCuoi;

    @OneToMany(mappedBy = "cuocTroChuyen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TinNhan> tinNhans;

    @OneToMany(mappedBy = "cuocTroChuyen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ThanhVienCuocTroChuyen> thanhViens;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
        soThanhVien = 0;
        if (loai == null) {
            loai = LoaiCuocTroChuyen.ca_nhan;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
} 
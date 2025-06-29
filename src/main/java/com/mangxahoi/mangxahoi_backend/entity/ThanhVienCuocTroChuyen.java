package com.mangxahoi.mangxahoi_backend.entity;

import com.mangxahoi.mangxahoi_backend.enums.VaiTroThanhVien;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "thanh_vien_cuoc_tro_chuyen")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThanhVienCuocTroChuyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuoc_tro_chuyen", nullable = false)
    private CuocTroChuyen cuocTroChuyen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "vai_tro")
    private VaiTroThanhVien vaiTro;

    @Column(name = "ngay_tham_gia")
    private LocalDateTime ngayThamGia;

    @PrePersist
    protected void onCreate() {
        ngayThamGia = LocalDateTime.now();
        vaiTro = VaiTroThanhVien.thanh_vien;
    }
} 
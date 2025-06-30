package com.mangxahoi.mangxahoi_backend.entity;

import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.enums.LoaiKetBan;
import com.mangxahoi.mangxahoi_backend.enums.TrangThaiKetBan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "ket_ban")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KetBan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_gui", nullable = false)
    private NguoiDung nguoiGui;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_nhan", nullable = false)
    private NguoiDung nguoiNhan;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiKetBan trangThai;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai")
    private LoaiKetBan loai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "ngay_huy_ketban")
    private LocalDateTime ngayHuyKetban;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
        if (trangThai == null) {
            trangThai = TrangThaiKetBan.cho_chap_nhan;
        }
        if (loai == null) {
            loai = LoaiKetBan.ket_ban;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
} 
package com.mangxahoi.mangxahoi_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "thong_bao")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongBao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_nhan", nullable = false)
    private NguoiDung nguoiNhan;

    @Column(name = "loai", nullable = false)
    private String loai;

    @Column(name = "tieu_de", nullable = false)
    private String tieuDe;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "da_doc")
    private Boolean daDoc;

    @Column(name = "da_xoa")
    private Boolean daXoa;

    @Column(name = "muc_do_uu_tien")
    private String mucDoUuTien;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_het_han")
    private LocalDateTime ngayHetHan;

    @OneToMany(mappedBy = "thongBao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ThongBaoTuongTac> thongBaoTuongTacs;

    @OneToMany(mappedBy = "thongBao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ThongBaoKetBan> thongBaoKetBans;

    @OneToMany(mappedBy = "thongBao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ThongBaoTinNhan> thongBaoTinNhans;

    @OneToMany(mappedBy = "thongBao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ThongBaoHeThong> thongBaoHeThongs;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        daDoc = false;
        daXoa = false;
        mucDoUuTien = "trung_binh";
    }
} 
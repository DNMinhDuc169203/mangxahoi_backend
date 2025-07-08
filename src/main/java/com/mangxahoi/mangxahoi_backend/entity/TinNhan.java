package com.mangxahoi.mangxahoi_backend.entity;

import com.mangxahoi.mangxahoi_backend.enums.LoaiTinNhan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tin_nhan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TinNhan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuoc_tro_chuyen", nullable = false)
    private CuocTroChuyen cuocTroChuyen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_gui", nullable = false)
    private NguoiDung nguoiGui;

    @Column(name = "noi_dung", columnDefinition = "TEXT", nullable = false)
    private String noiDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_tin_nhan")
    private LoaiTinNhan loaiTinNhan;

    @Column(name = "url_tep_tin")
    private String urlTepTin;

    @Column(name = "da_doc")
    private Boolean daDoc;

    @Column(name = "da_xoa")
    private Boolean daXoa;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        daDoc = false;
        daXoa = false;
        if (loaiTinNhan == null) {
            loaiTinNhan = LoaiTinNhan.van_ban;
        }
    }
} 
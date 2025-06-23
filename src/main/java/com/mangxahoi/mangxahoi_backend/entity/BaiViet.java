package com.mangxahoi.mangxahoi_backend.entity;

import com.mangxahoi.mangxahoi_backend.enums.CheDoBaiViet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bai_viet")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaiViet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "noi_dung", columnDefinition = "TEXT", nullable = false)
    private String noiDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "che_do_rieng_tu")
    private CheDoBaiViet cheDoRiengTu;

    @Column(name = "dang_xu_huong")
    private Boolean dangXuHuong;

    @Column(name = "so_luot_xem")
    private Integer soLuotXem;

    @Column(name = "so_luot_thich")
    private Integer soLuotThich;

    @Column(name = "so_luot_binh_luan")
    private Integer soLuotBinhLuan;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @OneToMany(mappedBy = "baiViet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BaiVietMedia> medias;

    @OneToMany(mappedBy = "baiViet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BinhLuan> binhLuans;

    @ManyToMany
    @JoinTable(
        name = "bai_viet_hashtag",
        joinColumns = @JoinColumn(name = "id_bai_viet"),
        inverseJoinColumns = @JoinColumn(name = "id_hashtag")
    )
    private List<Hashtag> hashtags;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
        soLuotXem = 0;
        soLuotThich = 0;
        soLuotBinhLuan = 0;
        dangXuHuong = false;
        if (cheDoRiengTu == null) {
            cheDoRiengTu = CheDoBaiViet.cong_khai;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
} 
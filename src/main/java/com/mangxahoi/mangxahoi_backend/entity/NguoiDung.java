package com.mangxahoi.mangxahoi_backend.entity;

import com.mangxahoi.mangxahoi_backend.enums.CheDoBaiViet;
import com.mangxahoi.mangxahoi_backend.enums.GioiTinh;
import com.mangxahoi.mangxahoi_backend.enums.LoaiMaXacThuc;
import com.mangxahoi.mangxahoi_backend.enums.VaiTro;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "nguoi_dung")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NguoiDung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "so_dien_thoai", nullable = false, unique = true, length = 10)
    private String soDienThoai;

    @Column(name = "mat_khau_hash", nullable = false)
    private String matKhauHash;

    @Column(name = "ho_ten", nullable = false, length = 50)
    private String hoTen;

    @Column(name = "tieu_su", columnDefinition = "TEXT")
    private String tieuSu;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private GioiTinh gioiTinh;

    private String diaChi;

    @Column(name = "da_xac_thuc")
    private Boolean daXacThuc;

    @Column(name = "dang_hoat_dong")
    private Boolean dangHoatDong;

    @Column(name = "bi_tam_khoa")
    private Boolean biTamKhoa;

    @Column(name = "ly_do_tam_khoa", columnDefinition = "TEXT")
    private String lyDoTamKhoa;

    @Column(name = "ngay_tam_khoa")
    private LocalDateTime ngayTamKhoa;

    @Column(name = "ngay_mo_khoa")
    private LocalDateTime ngayMoKhoa;

    @Column(name = "bi_xoa_mem")
    private Boolean biXoaMem;

    @Column(name = "ngay_xoa_mem")
    private LocalDateTime ngayXoaMem;

    @Enumerated(EnumType.STRING)
    @Column(name = "muc_rieng_tu")
    private CheDoBaiViet mucRiengTu;

    @Column(name = "token_xac_thuc")
    private String tokenXacThuc;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_ma_xac_thuc")
    private LoaiMaXacThuc loaiMaXacThuc;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "lan_dang_nhap_cuoi")
    private LocalDateTime lanDangNhapCuoi;

    @Column(name = "so_ban_be")
    private Integer soBanBe;

    @Column(name = "so_bai_dang")
    private Integer soBaiDang;

    @Enumerated(EnumType.STRING)
    private VaiTro vaiTro;

    @ColumnDefault("true")
    private Boolean emailCongKhai = true;

    @ColumnDefault("true")
    private Boolean sdtCongKhai = true;

    @ColumnDefault("true")
    private Boolean ngaySinhCongKhai = true;

    @ColumnDefault("true")
    private Boolean gioiTinhCongKhai = true;

    @OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BaiViet> baiViets;

    @OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NguoiDungAnh> anhDaiDien;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
        daXacThuc = false;
        dangHoatDong = true;
        biTamKhoa = false;
        biXoaMem = false;
        soBanBe = 0;
        soBaiDang = 0;
        vaiTro = VaiTro.nguoi_dung;
        mucRiengTu = CheDoBaiViet.cong_khai;
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
} 
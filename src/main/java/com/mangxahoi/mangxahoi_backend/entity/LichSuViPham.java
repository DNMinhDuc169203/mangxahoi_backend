package com.mangxahoi.mangxahoi_backend.entity;

import com.mangxahoi.mangxahoi_backend.enums.LoaiViPham;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_vi_pham")
public class LichSuViPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "noi_dung_vi_pham", columnDefinition = "TEXT")
    private String noiDungViPham;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_vi_pham")
    private LoaiViPham loaiViPham;

    @Column(name = "thoi_gian_vi_pham")
    private LocalDateTime thoiGianViPham;

    @Column(name = "hinh_phat")
    private String hinhPhat;

    @Column(name = "trang_thai_xu_ly")
    private String trangThaiXuLy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_xu_ly_id")
    private NguoiDung adminXuLy;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bao_cao_id")
    private BaoCao baoCaoLienQuan;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public NguoiDung getNguoiDung() { return nguoiDung; }
    public void setNguoiDung(NguoiDung nguoiDung) { this.nguoiDung = nguoiDung; }

    public String getNoiDungViPham() { return noiDungViPham; }
    public void setNoiDungViPham(String noiDungViPham) { this.noiDungViPham = noiDungViPham; }

    public LoaiViPham getLoaiViPham() { return loaiViPham; }
    public void setLoaiViPham(LoaiViPham loaiViPham) { this.loaiViPham = loaiViPham; }

    public LocalDateTime getThoiGianViPham() { return thoiGianViPham; }
    public void setThoiGianViPham(LocalDateTime thoiGianViPham) { this.thoiGianViPham = thoiGianViPham; }

    public String getHinhPhat() { return hinhPhat; }
    public void setHinhPhat(String hinhPhat) { this.hinhPhat = hinhPhat; }

    public String getTrangThaiXuLy() { return trangThaiXuLy; }
    public void setTrangThaiXuLy(String trangThaiXuLy) { this.trangThaiXuLy = trangThaiXuLy; }

    public NguoiDung getAdminXuLy() { return adminXuLy; }
    public void setAdminXuLy(NguoiDung adminXuLy) { this.adminXuLy = adminXuLy; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public BaoCao getBaoCaoLienQuan() { return baoCaoLienQuan; }
    public void setBaoCaoLienQuan(BaoCao baoCaoLienQuan) { this.baoCaoLienQuan = baoCaoLienQuan; }
} 
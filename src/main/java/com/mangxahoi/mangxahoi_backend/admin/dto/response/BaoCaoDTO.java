package com.mangxahoi.mangxahoi_backend.admin.dto.response;

import java.time.LocalDateTime;

public class BaoCaoDTO {
    private Integer id;
    private String loaiBaoCao;
    private String noiDung;
    private LocalDateTime ngayTao;
    private String tenNguoiBaoCao;
    private String tenNguoiBiBaoCao;
    private String trangThai;
    private LocalDateTime ngayGui;
    private String loaiDoiTuongBiBaoCao;
    private String noiDungDoiTuongBiBaoCao;
    // Có thể bổ sung thêm trường nếu cần

    public BaoCaoDTO() {}

    public BaoCaoDTO(Integer id, String loaiBaoCao, String noiDung, LocalDateTime ngayTao, String tenNguoiBaoCao, String tenNguoiBiBaoCao) {
        this.id = id;
        this.loaiBaoCao = loaiBaoCao;
        this.noiDung = noiDung;
        this.ngayTao = ngayTao;
        this.tenNguoiBaoCao = tenNguoiBaoCao;
        this.tenNguoiBiBaoCao = tenNguoiBiBaoCao;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getLoaiBaoCao() { return loaiBaoCao; }
    public void setLoaiBaoCao(String loaiBaoCao) { this.loaiBaoCao = loaiBaoCao; }
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
    public String getTenNguoiBaoCao() { return tenNguoiBaoCao; }
    public void setTenNguoiBaoCao(String tenNguoiBaoCao) { this.tenNguoiBaoCao = tenNguoiBaoCao; }
    public String getTenNguoiBiBaoCao() { return tenNguoiBiBaoCao; }
    public void setTenNguoiBiBaoCao(String tenNguoiBiBaoCao) { this.tenNguoiBiBaoCao = tenNguoiBiBaoCao; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public LocalDateTime getNgayGui() { return ngayGui; }
    public void setNgayGui(LocalDateTime ngayGui) { this.ngayGui = ngayGui; }
    public String getLoaiDoiTuongBiBaoCao() { return loaiDoiTuongBiBaoCao; }
    public void setLoaiDoiTuongBiBaoCao(String loaiDoiTuongBiBaoCao) { this.loaiDoiTuongBiBaoCao = loaiDoiTuongBiBaoCao; }
    public String getNoiDungDoiTuongBiBaoCao() { return noiDungDoiTuongBiBaoCao; }
    public void setNoiDungDoiTuongBiBaoCao(String noiDungDoiTuongBiBaoCao) { this.noiDungDoiTuongBiBaoCao = noiDungDoiTuongBiBaoCao; }
} 
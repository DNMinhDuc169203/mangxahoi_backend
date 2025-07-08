package com.mangxahoi.mangxahoi_backend.admin.dto.response;

import java.time.LocalDateTime;

public class BaoCaoDTO {
    private Integer id;
    private String loaiBaoCao;
    private String noiDung;
    private LocalDateTime ngayTao;
    private String tenNguoiBaoCao;
    private String tenNguoiBiBaoCao;
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
} 
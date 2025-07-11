package com.mangxahoi.mangxahoi_backend.admin.dto.response;

public class ChinhSachDTO {
    private Integer id;
    private String tieuDe;
    private String noiDung;
    private String ngayCapNhat;
    private Integer adminId;
    private String adminHoTen;

    public ChinhSachDTO() {}
    public ChinhSachDTO(Integer id, String tieuDe, String noiDung, String ngayCapNhat, Integer adminId, String adminHoTen) {
        this.id = id;
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.ngayCapNhat = ngayCapNhat;
        this.adminId = adminId;
        this.adminHoTen = adminHoTen;
    }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    public String getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(String ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }
    public Integer getAdminId() { return adminId; }
    public void setAdminId(Integer adminId) { this.adminId = adminId; }
    public String getAdminHoTen() { return adminHoTen; }
    public void setAdminHoTen(String adminHoTen) { this.adminHoTen = adminHoTen; }
} 
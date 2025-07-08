package com.mangxahoi.mangxahoi_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_xu_ly_bai_viet")
public class LichSuXuLyBaiViet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bai_viet_id", nullable = false)
    private BaiViet baiViet;

    @Column(name = "hanh_dong")
    private String hanhDong; // an, hien, xoa

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_xu_ly_id")
    private NguoiDung adminXuLy;

    @Column(name = "ly_do", columnDefinition = "TEXT")
    private String lyDo;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public BaiViet getBaiViet() { return baiViet; }
    public void setBaiViet(BaiViet baiViet) { this.baiViet = baiViet; }

    public String getHanhDong() { return hanhDong; }
    public void setHanhDong(String hanhDong) { this.hanhDong = hanhDong; }

    public LocalDateTime getThoiGian() { return thoiGian; }
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }

    public NguoiDung getAdminXuLy() { return adminXuLy; }
    public void setAdminXuLy(NguoiDung adminXuLy) { this.adminXuLy = adminXuLy; }

    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
} 
package com.mangxahoi.mangxahoi_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_goi_y")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LichSuGoiY {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_duoc_goi_y", nullable = false)
    private NguoiDung nguoiDuocGoiY;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_trong_goi_y", nullable = false)
    private NguoiDung nguoiTrongGoiY;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_diem_tuong_tac", nullable = false)
    private DiemTuongTac diemTuongTac;

    @Column(name = "ly_do_goi_y", columnDefinition = "json")
    private String lyDoGoiY;

    @Column(name = "diem_goi_y")
    private Integer diemGoiY;

    @Column(name = "thu_tu_goi_y")
    private Integer thuTuGoiY;

    @Column(name = "nguon_goi_y")
    private String nguonGoiY;

    @Column(name = "da_xem")
    private Boolean daXem;

    @Column(name = "thoi_gian_xem")
    private Integer thoiGianXem;

    @Column(name = "da_gui_loi_moi")
    private Boolean daGuiLoiMoi;

    @Column(name = "da_bo_qua")
    private Boolean daBoQua;

    @Column(name = "da_chan")
    private Boolean daChan;

    @Column(name = "ngay_goi_y")
    private LocalDateTime ngayGoiY;

    @Column(name = "ngay_xem")
    private LocalDateTime ngayXem;

    @Column(name = "ngay_hanh_dong")
    private LocalDateTime ngayHanhDong;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void onCreate() {
        ngayGoiY = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
        diemGoiY = 0;
        thuTuGoiY = 0;
        nguonGoiY = "hybrid";
        daXem = false;
        thoiGianXem = 0;
        daGuiLoiMoi = false;
        daBoQua = false;
        daChan = false;
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
} 
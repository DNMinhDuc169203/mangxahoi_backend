package com.mangxahoi.mangxahoi_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "diem_tuong_tac")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiemTuongTac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_1", nullable = false)
    private NguoiDung nguoi1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_2", nullable = false)
    private NguoiDung nguoi2;

    @Column(name = "diem_ban_chung")
    private Integer diemBanChung;

    @Column(name = "diem_dia_ly")
    private Integer diemDiaLy;

    @Column(name = "diem_so_thich")
    private Integer diemSoThich;

    @Column(name = "diem_thoi_gian")
    private Integer diemThoiGian;

    @Column(name = "diem_tuong_tac_gan_day")
    private Integer diemTuongTacGanDay;

    @Column(name = "diem_tuong_tac_thuong_xuyen")
    private Integer diemTuongTacThuongXuyen;

    @Column(name = "tong_diem")
    private Integer tongDiem;

    @Column(name = "so_lan_tuong_tac")
    private Integer soLanTuongTac;

    @Column(name = "lan_tuong_tac_cuoi")
    private LocalDateTime lanTuongTacCuoi;

    @Column(name = "muc_do_than_thiet")
    private String mucDoThanThiet;

    @Column(name = "trang_thai_goi_y")
    private String trangThaiGoiY;

    @Column(name = "lan_cap_nhat_cuoi")
    private LocalDateTime lanCapNhatCuoi;

    @OneToMany(mappedBy = "diemTuongTac", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<LichSuGoiY> lichSuGoiYs;

    @PrePersist
    protected void onCreate() {
        diemBanChung = 0;
        diemDiaLy = 0;
        diemSoThich = 0;
        diemThoiGian = 0;
        diemTuongTacGanDay = 0;
        diemTuongTacThuongXuyen = 0;
        tongDiem = 0;
        soLanTuongTac = 0;
        mucDoThanThiet = "xa_la";
        trangThaiGoiY = "chua_goi_y";
        lanCapNhatCuoi = LocalDateTime.now();
    }
} 
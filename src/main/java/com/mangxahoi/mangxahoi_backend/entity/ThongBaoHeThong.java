package com.mangxahoi.mangxahoi_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "thong_bao_he_thong")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongBaoHeThong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thong_bao", nullable = false)
    private ThongBao thongBao;

    @Column(name = "loai_he_thong", nullable = false)
    private String loaiHeThong;

    @Column(name = "url_hanh_dong")
    private String urlHanhDong;
} 
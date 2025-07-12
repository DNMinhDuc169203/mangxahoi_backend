package com.mangxahoi.mangxahoi_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "saved_post")
public class SavedPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bai_viet_id", nullable = false)
    private BaiViet baiViet;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public NguoiDung getNguoiDung() { return nguoiDung; }
    public void setNguoiDung(NguoiDung nguoiDung) { this.nguoiDung = nguoiDung; }

    public BaiViet getBaiViet() { return baiViet; }
    public void setBaiViet(BaiViet baiViet) { this.baiViet = baiViet; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
} 
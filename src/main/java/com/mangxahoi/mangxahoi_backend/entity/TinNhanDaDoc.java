package com.mangxahoi.mangxahoi_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tin_nhan_da_doc")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class TinNhanDaDoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tin_nhan")
    private TinNhan tinNhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_doc")
    private NguoiDung nguoiDoc;

    @Column(name = "thoi_gian_doc")
    private LocalDateTime thoiGianDoc;
} 
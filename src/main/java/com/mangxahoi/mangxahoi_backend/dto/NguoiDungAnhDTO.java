package com.mangxahoi.mangxahoi_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NguoiDungAnhDTO {
    private Integer id;
    private String url;
    private boolean laAnhChinh;
    private LocalDateTime ngayTao;
} 
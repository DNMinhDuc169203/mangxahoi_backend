package com.mangxahoi.mangxahoi_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ThongBaoDTO {
    private Integer id;
    private String loai;
    private String tieuDe;
    private String noiDung;
    private Boolean daDoc;
    private String mucDoUuTien;
    private LocalDateTime ngayTao;
    private Integer nguoiNhanId;
    private String nguoiNhanTen;
} 
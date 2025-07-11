package com.mangxahoi.mangxahoi_backend.dto.response;

import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LichSuGoiYDTO {
    private Integer id;
    private NguoiDungDTO nguoiTrongGoiY;
    private Integer diemGoiY;
    private String lyDoGoiY;
    private Boolean daGuiLoiMoi;
    private Boolean daBoQua;
    private Boolean daChan;
    private LocalDateTime ngayGoiY;
} 
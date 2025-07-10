package com.mangxahoi.mangxahoi_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NguoiDocDTO {
    private Integer id;
    private String hoTen;
    private String anhDaiDien;
} 
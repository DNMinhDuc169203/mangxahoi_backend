package com.mangxahoi.mangxahoi_backend.controller;

import com.mangxahoi.mangxahoi_backend.entity.ChinhSach;
import com.mangxahoi.mangxahoi_backend.admin.service.ChinhSachService;
import com.mangxahoi.mangxahoi_backend.admin.dto.response.ChinhSachDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chinh-sach")
@RequiredArgsConstructor
public class ChinhSachController {
    private final ChinhSachService chinhSachService;

    @GetMapping("/moi-nhat")
    public ResponseEntity<ChinhSachDTO> layChinhSachMoiNhat() {
        ChinhSachDTO dto = chinhSachService.layChinhSachMoiNhatDTO();
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
} 
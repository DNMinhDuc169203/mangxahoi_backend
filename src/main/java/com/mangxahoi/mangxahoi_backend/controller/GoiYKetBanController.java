package com.mangxahoi.mangxahoi_backend.controller;

import com.mangxahoi.mangxahoi_backend.service.GoiYKetBanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/goi-y")
@RequiredArgsConstructor
public class GoiYKetBanController {

    private final GoiYKetBanService goiYKetBanService;
    
    @PostMapping("/tao-goi-y/{idNguoiDung}")
    public ResponseEntity<String> taoGoiYKetBan(@PathVariable Integer idNguoiDung) {
        goiYKetBanService.taoGoiYKetBan(idNguoiDung);
        return ResponseEntity.ok("Đã tạo gợi ý kết bạn thành công");
    }
} 
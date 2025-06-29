package com.mangxahoi.mangxahoi_backend.service.impl;

import com.mangxahoi.mangxahoi_backend.repository.GoiYKetBanRepository;
import com.mangxahoi.mangxahoi_backend.service.GoiYKetBanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoiYKetBanServiceImpl implements GoiYKetBanService {

    private final GoiYKetBanRepository goiYKetBanRepository;
    
    @Override
    @Transactional
    public void taoGoiYKetBan(Integer idNguoiDung) {
        // Gọi stored procedure để tính toán gợi ý bạn bè
        goiYKetBanRepository.calculateFriendSuggestions(idNguoiDung);
    }
} 
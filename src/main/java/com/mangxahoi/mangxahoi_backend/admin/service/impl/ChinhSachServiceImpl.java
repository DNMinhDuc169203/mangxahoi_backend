package com.mangxahoi.mangxahoi_backend.admin.service.impl;

import com.mangxahoi.mangxahoi_backend.admin.service.ChinhSachService;
import com.mangxahoi.mangxahoi_backend.entity.ChinhSach;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.repository.ChinhSachRepository;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChinhSachServiceImpl implements ChinhSachService {
    private final ChinhSachRepository chinhSachRepository;
    private final NguoiDungRepository nguoiDungRepository;

    @Override
    @Transactional
    public ChinhSach taoChinhSach(String tieuDe, String noiDung, Integer adminId) {
        NguoiDung admin = nguoiDungRepository.findById(adminId).orElse(null);
        ChinhSach cs = new ChinhSach();
        cs.setTieuDe(tieuDe);
        cs.setNoiDung(noiDung);
        cs.setNgayCapNhat(LocalDateTime.now());
        cs.setAdminCapNhat(admin);
        return chinhSachRepository.save(cs);
    }

    @Override
    @Transactional
    public ChinhSach capNhatChinhSach(Integer id, String tieuDe, String noiDung, Integer adminId) {
        ChinhSach cs = chinhSachRepository.findById(id).orElseThrow();
        NguoiDung admin = nguoiDungRepository.findById(adminId).orElse(null);
        cs.setTieuDe(tieuDe);
        cs.setNoiDung(noiDung);
        cs.setNgayCapNhat(LocalDateTime.now());
        cs.setAdminCapNhat(admin);
        return chinhSachRepository.save(cs);
    }

    @Override
    public List<ChinhSach> layDanhSachChinhSach() {
        return chinhSachRepository.findAll();
    }

    @Override
    public ChinhSach layChiTietChinhSach(Integer id) {
        return chinhSachRepository.findById(id).orElse(null);
    }

    @Override
    public ChinhSach layChinhSachMoiNhat() {
        return chinhSachRepository.findTopByOrderByNgayCapNhatDesc();
    }
} 
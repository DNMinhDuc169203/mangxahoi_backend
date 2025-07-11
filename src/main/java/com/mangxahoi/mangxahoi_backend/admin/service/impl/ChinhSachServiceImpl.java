package com.mangxahoi.mangxahoi_backend.admin.service.impl;

import com.mangxahoi.mangxahoi_backend.admin.dto.response.ChinhSachDTO;
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
import java.util.stream.Collectors;

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
    public ChinhSachDTO capNhatChinhSach(Integer id, String tieuDe, String noiDung, Integer adminId) {
        ChinhSach cs = chinhSachRepository.findById(id).orElseThrow();
        NguoiDung admin = nguoiDungRepository.findById(adminId).orElse(null);
        cs.setTieuDe(tieuDe);
        cs.setNoiDung(noiDung);
        cs.setNgayCapNhat(java.time.LocalDateTime.now());
        cs.setAdminCapNhat(admin);
        ChinhSach updated = chinhSachRepository.save(cs);
        return new com.mangxahoi.mangxahoi_backend.admin.dto.response.ChinhSachDTO(
            updated.getId(),
            updated.getTieuDe(),
            updated.getNoiDung(),
            updated.getNgayCapNhat() != null ? updated.getNgayCapNhat().toString() : null,
            updated.getAdminCapNhat() != null ? updated.getAdminCapNhat().getId() : null,
            updated.getAdminCapNhat() != null ? updated.getAdminCapNhat().getHoTen() : null
        );
    }

    @Override
    public List<ChinhSachDTO> layDanhSachChinhSach() {
        return chinhSachRepository.findAll().stream()
            .map(cs -> new ChinhSachDTO(
                cs.getId(),
                cs.getTieuDe(),
                cs.getNoiDung(),
                cs.getNgayCapNhat() != null ? cs.getNgayCapNhat().toString() : null,
                cs.getAdminCapNhat() != null ? cs.getAdminCapNhat().getId() : null,
                cs.getAdminCapNhat() != null ? cs.getAdminCapNhat().getHoTen() : null
            ))
            .collect(Collectors.toList());
    }

    @Override
    public ChinhSach layChiTietChinhSach(Integer id) {
        return chinhSachRepository.findById(id).orElse(null);
    }

    @Override
    public ChinhSach layChinhSachMoiNhat() {
        return chinhSachRepository.findTopByOrderByNgayCapNhatDesc();
    }

    @Override
    public ChinhSachDTO layChinhSachMoiNhatDTO() {
        ChinhSach cs = chinhSachRepository.findTopByOrderByNgayCapNhatDesc();
        if (cs == null) return null;
        return new ChinhSachDTO(
            cs.getId(),
            cs.getTieuDe(),
            cs.getNoiDung(),
            cs.getNgayCapNhat() != null ? cs.getNgayCapNhat().toString() : null,
            cs.getAdminCapNhat() != null ? cs.getAdminCapNhat().getId() : null,
            cs.getAdminCapNhat() != null ? cs.getAdminCapNhat().getHoTen() : null
        );
    }

    @Override
    @Transactional
    public void xoaChinhSach(Integer id, Integer adminId) {
        // Có thể kiểm tra quyền adminId nếu cần
        chinhSachRepository.deleteById(id);
    }
} 
package com.mangxahoi.mangxahoi_backend.service.impl;

import com.mangxahoi.mangxahoi_backend.entity.BaoCao;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.enums.TrangThaiBaoCao;
import com.mangxahoi.mangxahoi_backend.repository.BaoCaoRepository;
import com.mangxahoi.mangxahoi_backend.repository.BaiVietRepository;
import com.mangxahoi.mangxahoi_backend.repository.BinhLuanRepository;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import com.mangxahoi.mangxahoi_backend.service.BaoCaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BaoCaoServiceImpl implements BaoCaoService {
    private final BaoCaoRepository baoCaoRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final BaiVietRepository baiVietRepository;
    private final BinhLuanRepository binhLuanRepository;

    @Override
    public ResponseEntity<?> guiBaoCao(BaoCao baoCao, NguoiDung nguoiDung) {
        baoCao.setNguoiBaoCao(nguoiDung);
        // Không cho phép tự báo cáo chính mình
        if (baoCao.getNguoiDungBiBaoCao() != null && baoCao.getNguoiDungBiBaoCao().getId() != null) {
            if (baoCao.getNguoiDungBiBaoCao().getId().equals(nguoiDung.getId())) {
                return ResponseEntity.badRequest().body("Không thể tự báo cáo chính mình");
            }
            nguoiDungRepository.findById(baoCao.getNguoiDungBiBaoCao().getId()).ifPresent(baoCao::setNguoiDungBiBaoCao);
        }
        // Kiểm tra đối tượng bị báo cáo (bài viết, bình luận)
        if (baoCao.getBaiViet() != null && baoCao.getBaiViet().getId() != null) {
            baiVietRepository.findById(baoCao.getBaiViet().getId()).ifPresent(baoCao::setBaiViet);
        }
        if (baoCao.getBinhLuan() != null && baoCao.getBinhLuan().getId() != null) {
            binhLuanRepository.findById(baoCao.getBinhLuan().getId()).ifPresent(baoCao::setBinhLuan);
        }
        baoCaoRepository.save(baoCao);
        return ResponseEntity.ok("Đã gửi báo cáo thành công");
    }

    @Override
    public ResponseEntity<?> layDanhSachBaoCao(Pageable pageable, String trangThai) {
        Page<BaoCao> page;
        if (trangThai != null) {
            try {
                TrangThaiBaoCao ttb = TrangThaiBaoCao.valueOf(trangThai);
                page = baoCaoRepository.findByTrangThai(ttb, pageable);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Trạng thái báo cáo không hợp lệ");
            }
        } else {
            page = baoCaoRepository.findAll(pageable);
        }
        return ResponseEntity.ok(page);
    }

    @Override
    public ResponseEntity<?> layBaoCaoNguoiDung(Integer idNguoiDung) {
        Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findById(idNguoiDung);
        if (nguoiDungOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        }
        return ResponseEntity.ok(baoCaoRepository.findByNguoiBaoCao(nguoiDungOpt.get()));
    }
} 
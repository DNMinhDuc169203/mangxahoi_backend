package com.mangxahoi.mangxahoi_backend.service.impl;

import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import com.mangxahoi.mangxahoi_backend.entity.KetBan;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDungAnh;
import com.mangxahoi.mangxahoi_backend.enums.TrangThaiKetBan;
import com.mangxahoi.mangxahoi_backend.exception.ResourceNotFoundException;
import com.mangxahoi.mangxahoi_backend.exception.ValidationException;
import com.mangxahoi.mangxahoi_backend.repository.KetBanRepository;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungAnhRepository;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import com.mangxahoi.mangxahoi_backend.service.KetBanService;
import com.mangxahoi.mangxahoi_backend.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KetBanServiceImpl implements KetBanService {

    private final KetBanRepository ketBanRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final NguoiDungAnhRepository nguoiDungAnhRepository;
    private final TokenUtil tokenUtil;

    private NguoiDung layNguoiDungTuToken(String token) {
        return tokenUtil.layNguoiDungTuToken(token);
    }

    private NguoiDung timNguoiDungBangId(Integer id) {
        return nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", id));
    }
    
    @Override
    @Transactional
    public boolean guiLoiMoiKetBan(Integer idNguoiGui, Integer idNguoiNhan) {
        if (idNguoiGui.equals(idNguoiNhan)) {
            throw new ValidationException("Bạn không thể tự kết bạn với chính mình");
        }
        NguoiDung nguoiGui = timNguoiDungBangId(idNguoiGui);
        NguoiDung nguoiNhan = timNguoiDungBangId(idNguoiNhan);

        ketBanRepository.findRelationship(nguoiGui, nguoiNhan).ifPresent(kb -> {
            throw new ValidationException("Yêu cầu kết bạn đã tồn tại hoặc đã là bạn bè.");
        });

        KetBan ketBan = new KetBan();
        ketBan.setNguoiGui(nguoiGui);
        ketBan.setNguoiNhan(nguoiNhan);
        ketBan.setTrangThai(TrangThaiKetBan.cho_chap_nhan);
        ketBanRepository.save(ketBan);
        return true;
    }

    @Override
    @Transactional
    public boolean chapNhanLoiMoiKetBan(Integer idNguoiDung, Integer idLoiMoi) {
        KetBan ketBan = ketBanRepository.findById(idLoiMoi)
                .orElseThrow(() -> new ResourceNotFoundException("Lời mời", "id", idLoiMoi));
        if (!ketBan.getNguoiNhan().getId().equals(idNguoiDung)) {
            throw new ValidationException("Bạn không có quyền chấp nhận lời mời này");
        }
        ketBan.setTrangThai(TrangThaiKetBan.ban_be);
        ketBanRepository.save(ketBan);
        return true;
    }

    @Override
    @Transactional
    public boolean tuChoiLoiMoiKetBan(Integer idNguoiDung, Integer idLoiMoi) {
        KetBan ketBan = ketBanRepository.findById(idLoiMoi)
                .orElseThrow(() -> new ResourceNotFoundException("Lời mời", "id", idLoiMoi));
        if (!ketBan.getNguoiNhan().getId().equals(idNguoiDung)) {
            throw new ValidationException("Bạn không có quyền từ chối lời mời này");
        }
        ketBanRepository.delete(ketBan);
        return true;
    }

    @Override
    @Transactional
    public boolean huyLoiMoiKetBan(Integer idNguoiDung, Integer idLoiMoi) {
        KetBan ketBan = ketBanRepository.findById(idLoiMoi)
                .orElseThrow(() -> new ResourceNotFoundException("Lời mời", "id", idLoiMoi));
        if (!ketBan.getNguoiGui().getId().equals(idNguoiDung)) {
            throw new ValidationException("Bạn không có quyền hủy lời mời này");
        }
        ketBanRepository.delete(ketBan);
        return true;
    }

    @Override
    @Transactional
    public boolean huyKetBan(Integer idNguoiDung1, Integer idNguoiDung2) {
        NguoiDung nguoi1 = timNguoiDungBangId(idNguoiDung1);
        NguoiDung nguoi2 = timNguoiDungBangId(idNguoiDung2);
        KetBan ketBan = ketBanRepository.areFriends(nguoi1, nguoi2)
                .orElseThrow(() -> new ValidationException("Hai người không phải là bạn bè"));
        ketBanRepository.delete(ketBan);
        return true;
    }

    @Override
    @Transactional
    public boolean chanNguoiDung(Integer idNguoiChan, Integer idNguoiBiChan) {
        if (idNguoiChan.equals(idNguoiBiChan)) {
            throw new ValidationException("Bạn không thể tự chặn chính mình.");
        }
        NguoiDung nguoiChan = timNguoiDungBangId(idNguoiChan);
        NguoiDung nguoiBiChan = timNguoiDungBangId(idNguoiBiChan);

        Optional<KetBan> relationshipOpt = ketBanRepository.findRelationship(nguoiChan, nguoiBiChan);
        KetBan relationship;
        if (relationshipOpt.isPresent()) {
            relationship = relationshipOpt.get();
        } else {
            relationship = new KetBan();
        }
        
        relationship.setNguoiGui(nguoiChan);
        relationship.setNguoiNhan(nguoiBiChan);
        relationship.setTrangThai(TrangThaiKetBan.bi_chan);
        ketBanRepository.save(relationship);
        return true;
    }

    @Override
    @Transactional
    public boolean boChanNguoiDung(Integer idNguoiChan, Integer idNguoiBiChan) {
        NguoiDung nguoiChan = timNguoiDungBangId(idNguoiChan);
        NguoiDung nguoiBiChan = timNguoiDungBangId(idNguoiBiChan);
        
        KetBan relationship = ketBanRepository.findByNguoiGuiAndNguoiNhan(nguoiChan, nguoiBiChan)
            .filter(kb -> kb.getTrangThai() == TrangThaiKetBan.bi_chan)
            .orElseThrow(() -> new ValidationException("Bạn chưa chặn người dùng này."));

        ketBanRepository.delete(relationship);
        return true;
    }

    @Override
    public Page<NguoiDungDTO> danhSachBanBe(Integer idNguoiDung, Pageable pageable) {
        NguoiDung nguoiDung = timNguoiDungBangId(idNguoiDung);
        Page<KetBan> banBePage = ketBanRepository.findFriends(nguoiDung, pageable);
        return banBePage.map(ketBan -> {
            NguoiDung friend = ketBan.getNguoiGui().getId().equals(idNguoiDung) ? ketBan.getNguoiNhan() : ketBan.getNguoiGui();
            return chuyenSangDTO(friend);
        });
    }

    @Override
    public Page<NguoiDungDTO> danhSachLoiMoiKetBan(Integer idNguoiDung, Pageable pageable) {
        NguoiDung nguoiDung = timNguoiDungBangId(idNguoiDung);
        Page<KetBan> loiMoiPage = ketBanRepository.findByNguoiNhanAndTrangThai(nguoiDung, TrangThaiKetBan.cho_chap_nhan, pageable);
        return loiMoiPage.map(ketBan -> chuyenSangDTO(ketBan.getNguoiGui()));
    }

    @Override
    public Page<NguoiDungDTO> danhSachLoiMoiDaGui(Integer idNguoiDung, Pageable pageable) {
        NguoiDung nguoiDung = timNguoiDungBangId(idNguoiDung);
        Page<KetBan> loiMoiDaGuiPage = ketBanRepository.findByNguoiGuiAndTrangThai(nguoiDung, TrangThaiKetBan.cho_chap_nhan, pageable);
        return loiMoiDaGuiPage.map(ketBan -> chuyenSangDTO(ketBan.getNguoiNhan()));
    }

    @Override
    public Page<NguoiDungDTO> danhSachNguoiDungBiChan(Integer idNguoiDung, Pageable pageable) {
        NguoiDung nguoiDung = timNguoiDungBangId(idNguoiDung);
        Page<KetBan> biChanPage = ketBanRepository.findByNguoiGuiAndTrangThai(nguoiDung, TrangThaiKetBan.bi_chan, pageable);
        return biChanPage.map(ketBan -> chuyenSangDTO(ketBan.getNguoiNhan()));
    }
    
    @Override
    public TrangThaiKetBan kiemTraTrangThaiKetBan(Integer idNguoiDung1, Integer idNguoiDung2) {
        NguoiDung nguoi1 = timNguoiDungBangId(idNguoiDung1);
        NguoiDung nguoi2 = timNguoiDungBangId(idNguoiDung2);
        Optional<KetBan> relationship = ketBanRepository.findRelationship(nguoi1, nguoi2);
        return relationship.map(KetBan::getTrangThai).orElse(null);
    }
    
    @Override
    public long demSoBanBe(Integer idNguoiDung) {
        NguoiDung nguoiDung = timNguoiDungBangId(idNguoiDung);
        return ketBanRepository.countFriends(nguoiDung);
    }

    @Override
    public long demSoLoiMoiKetBan(Integer idNguoiDung) {
        NguoiDung nguoiDung = timNguoiDungBangId(idNguoiDung);
        return ketBanRepository.countPendingFriendRequests(nguoiDung);
    }

    @Override
    public Page<NguoiDungDTO> goiYKetBan(Integer idNguoiDung, Pageable pageable) {
        // Logic gợi ý phức tạp, tạm thời trả về rỗng
        return Page.empty(pageable);
    }

    private NguoiDungDTO chuyenSangDTO(NguoiDung nguoiDung) {
        String anhDaiDien = nguoiDungAnhRepository.findByNguoiDungAndLaAnhChinh(nguoiDung, true)
                .map(NguoiDungAnh::getUrl)
                .orElse(null);

        return NguoiDungDTO.builder()
                .id(nguoiDung.getId())
                .email(nguoiDung.getEmail())
                .soDienThoai(nguoiDung.getSoDienThoai())
                .hoTen(nguoiDung.getHoTen())
                .tieuSu(nguoiDung.getTieuSu())
                .ngaySinh(nguoiDung.getNgaySinh())
                .gioiTinh(nguoiDung.getGioiTinh())
                .diaChi(nguoiDung.getDiaChi())
                .daXacThuc(nguoiDung.getDaXacThuc())
                .dangHoatDong(nguoiDung.getDangHoatDong())
                .mucRiengTu(nguoiDung.getMucRiengTu())
                .ngayTao(nguoiDung.getNgayTao())
                .lanDangNhapCuoi(nguoiDung.getLanDangNhapCuoi())
                .soBanBe(nguoiDung.getSoBanBe())
                .soBaiDang(nguoiDung.getSoBaiDang())
                .vaiTro(nguoiDung.getVaiTro())
                .anhDaiDien(anhDaiDien)
                .build();
    }
} 
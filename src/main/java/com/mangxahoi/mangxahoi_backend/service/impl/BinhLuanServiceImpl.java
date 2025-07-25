package com.mangxahoi.mangxahoi_backend.service.impl;

import com.mangxahoi.mangxahoi_backend.dto.BinhLuanDTO;
import com.mangxahoi.mangxahoi_backend.entity.BaiViet;
import com.mangxahoi.mangxahoi_backend.entity.BinhLuan;
import com.mangxahoi.mangxahoi_backend.entity.LuotThichBinhLuan;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDungAnh;
import com.mangxahoi.mangxahoi_backend.entity.ThongBao;
import com.mangxahoi.mangxahoi_backend.entity.ChiTietTuongTac;
import com.mangxahoi.mangxahoi_backend.enums.LoaiThongBao;
import com.mangxahoi.mangxahoi_backend.enums.LoaiTuongTac;
import com.mangxahoi.mangxahoi_backend.exception.ResourceNotFoundException;
import com.mangxahoi.mangxahoi_backend.repository.BaiVietRepository;
import com.mangxahoi.mangxahoi_backend.repository.BinhLuanRepository;
import com.mangxahoi.mangxahoi_backend.repository.LuotThichBinhLuanRepository;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import com.mangxahoi.mangxahoi_backend.repository.ThongBaoRepository;
import com.mangxahoi.mangxahoi_backend.repository.ChiTietTuongTacRepository;
import com.mangxahoi.mangxahoi_backend.service.BinhLuanService;
import com.mangxahoi.mangxahoi_backend.service.GoiYKetBanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BinhLuanServiceImpl implements BinhLuanService {
    private static final Logger logger = LoggerFactory.getLogger(BinhLuanServiceImpl.class);

    private final BinhLuanRepository binhLuanRepository;
    private final BaiVietRepository baiVietRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final LuotThichBinhLuanRepository luotThichBinhLuanRepository;
    private final ThongBaoRepository thongBaoRepository;
    private final GoiYKetBanService goiYKetBanService;
    private final ChiTietTuongTacRepository chiTietTuongTacRepository;


    @Override
    @Transactional
    public BinhLuanDTO themBinhLuan(Integer idBaiViet, Integer idNguoiDung, Integer idBinhLuanCha, BinhLuanDTO binhLuanDTO) {
        // Tìm bài viết
        BaiViet baiViet = baiVietRepository.findById(idBaiViet)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết", "id", idBaiViet));
        
        // Tìm người dùng
        NguoiDung nguoiDung = nguoiDungRepository.findById(idNguoiDung)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", idNguoiDung));
        
        // Tìm bình luận cha nếu có
        BinhLuan binhLuanCha = null;
        if (idBinhLuanCha != null) {
            binhLuanCha = binhLuanRepository.findById(idBinhLuanCha)
                    .orElseThrow(() -> new ResourceNotFoundException("Bình luận cha", "id", idBinhLuanCha));
            
            // Kiểm tra bình luận cha thuộc bài viết
            if (!binhLuanCha.getBaiViet().getId().equals(idBaiViet)) {
                throw new IllegalArgumentException("Bình luận cha không thuộc bài viết này");
            }
        }
        
        // Tạo bình luận mới
        BinhLuan binhLuan = new BinhLuan();
        binhLuan.setBaiViet(baiViet);
        binhLuan.setNguoiDung(nguoiDung);
        binhLuan.setBinhLuanCha(binhLuanCha);
        binhLuan.setNoiDung(binhLuanDTO.getNoiDung());
        
        // Lưu bình luận
        BinhLuan savedBinhLuan = binhLuanRepository.save(binhLuan);
        
        // Tăng số lượt bình luận của bài viết
        baiViet.setSoLuotBinhLuan(baiViet.getSoLuotBinhLuan() + 1);
        baiVietRepository.save(baiViet);
        
        // Tự động cập nhật gợi ý kết bạn
        logger.info("[BinhLuan] Gọi gợi ý kết bạn cho userId: {}", idNguoiDung);
        goiYKetBanService.taoGoiYKetBan(idNguoiDung);
        if (!baiViet.getNguoiDung().getId().equals(idNguoiDung)) {
            logger.info("[BinhLuan] Gọi gợi ý kết bạn cho chủ bài viết userId: {}", baiViet.getNguoiDung().getId());
            goiYKetBanService.taoGoiYKetBan(baiViet.getNguoiDung().getId());
        }
        
        // GỬI THÔNG BÁO TỰ ĐỘNG
        // Đã bỏ logic gửi thông báo ở đây
        
        // Ghi tương tác vào chi_tiet_tuong_tac
        ChiTietTuongTac chiTiet = new ChiTietTuongTac();
        chiTiet.setNguoi1(nguoiDung);
        chiTiet.setNguoi2(baiViet.getNguoiDung());
        chiTiet.setLoaiTuongTac(LoaiTuongTac.binh_luan);
        chiTiet.setDiemTuongTac(10);
        chiTiet.setNgayTao(java.time.LocalDateTime.now());
        chiTietTuongTacRepository.save(chiTiet);
        
        // Chuyển đổi sang DTO và trả về
        return convertToDTO(savedBinhLuan, idNguoiDung);
    }

    @Override
    @Transactional
    public BinhLuanDTO capNhatBinhLuan(Integer id, Integer idNguoiDung, BinhLuanDTO binhLuanDTO) {
        // Tìm bình luận
        BinhLuan binhLuan = binhLuanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bình luận", "id", id));
        
        // Kiểm tra quyền sửa bình luận
        if (!binhLuan.getNguoiDung().getId().equals(idNguoiDung)) {
            throw new IllegalArgumentException("Không có quyền sửa bình luận này");
        }
        
        // Cập nhật thông tin
        binhLuan.setNoiDung(binhLuanDTO.getNoiDung());
        
        // Lưu bình luận
        BinhLuan updatedBinhLuan = binhLuanRepository.save(binhLuan);
        
        // Chuyển đổi sang DTO và trả về
        return convertToDTO(updatedBinhLuan, idNguoiDung);
    }

    @Override
    @Transactional
    public boolean xoaBinhLuan(Integer id, Integer idNguoiDung) {
        // Tìm bình luận
        BinhLuan binhLuan = binhLuanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bình luận", "id", id));
        
        // Kiểm tra quyền xóa bình luận: cho phép người viết bình luận hoặc chủ bài viết
        if (
            !binhLuan.getNguoiDung().getId().equals(idNguoiDung) &&
            !binhLuan.getBaiViet().getNguoiDung().getId().equals(idNguoiDung)
        ) {
            return false;
        }
        
        // Lấy bài viết để cập nhật số lượt bình luận
        BaiViet baiViet = binhLuan.getBaiViet();
        
        // Đếm số bình luận sẽ bị xóa (bao gồm cả bình luận phản hồi)
        long soLuongBinhLuan = 1; // Bản thân bình luận
        soLuongBinhLuan += binhLuanRepository.countByBinhLuanCha(binhLuan); // Các bình luận phản hồi
        
        // Xóa bình luận (cascade sẽ xóa các bình luận phản hồi và lượt thích)
        binhLuanRepository.delete(binhLuan);
        
        // Giảm số lượt bình luận của bài viết
        baiViet.setSoLuotBinhLuan(Math.max(0, baiViet.getSoLuotBinhLuan() - (int) soLuongBinhLuan));
        baiVietRepository.save(baiViet);
        
        return true;
    }

    @Override
    public Page<BinhLuanDTO> layBinhLuanGocTheoBaiViet(Integer idBaiViet, Integer idNguoiDungHienTai, Pageable pageable) {
        // Tìm bài viết
        BaiViet baiViet = baiVietRepository.findById(idBaiViet)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết", "id", idBaiViet));
        
        // Lấy danh sách bình luận gốc (không có bình luận cha)
        Page<BinhLuan> binhLuanPage = binhLuanRepository.findByBaiVietAndBinhLuanChaIsNull(baiViet, pageable);
        
        // Chuyển đổi sang DTO
        List<BinhLuanDTO> binhLuanDTOs = binhLuanPage.getContent().stream()
                .map(binhLuan -> {
                    BinhLuanDTO dto = convertToDTO(binhLuan, idNguoiDungHienTai);
                    long soLuotPhanHoi = binhLuanRepository.countByBinhLuanCha(binhLuan);
                    dto.setSoLuotPhanHoi((int) soLuotPhanHoi);
                    return dto;
                })
                .collect(Collectors.toList());
        
        return new PageImpl<>(binhLuanDTOs, pageable, binhLuanPage.getTotalElements());
    }

    @Override
    public Page<BinhLuanDTO> layBinhLuanPhanHoi(Integer idBinhLuanCha, Integer idNguoiDungHienTai, Pageable pageable) {
        // Tìm bình luận cha
        BinhLuan binhLuanCha = binhLuanRepository.findById(idBinhLuanCha)
                .orElseThrow(() -> new ResourceNotFoundException("Bình luận cha", "id", idBinhLuanCha));
        
        // Lấy danh sách bình luận phản hồi
        Page<BinhLuan> binhLuanPage = binhLuanRepository.findByBinhLuanCha(binhLuanCha, pageable);
        
        // Chuyển đổi sang DTO
        List<BinhLuanDTO> binhLuanDTOs = binhLuanPage.getContent().stream()
                .map(binhLuan -> convertToDTO(binhLuan, idNguoiDungHienTai))
                .collect(Collectors.toList());
        
        return new PageImpl<>(binhLuanDTOs, pageable, binhLuanPage.getTotalElements());
    }
@Override
@Transactional
public boolean thichBinhLuan(Integer idBinhLuan, Integer idNguoiDung) {
    // Tìm bình luận
    BinhLuan binhLuan = binhLuanRepository.findById(idBinhLuan)
            .orElseThrow(() -> new ResourceNotFoundException("Bình luận", "id", idBinhLuan));
    
    // Tìm người dùng
    NguoiDung nguoiDung = nguoiDungRepository.findById(idNguoiDung)
            .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", idNguoiDung));
    
    // Kiểm tra xem người dùng đã thích bình luận này chưa
    Optional<LuotThichBinhLuan> existingLike = luotThichBinhLuanRepository.findByNguoiDungAndBinhLuan(nguoiDung, binhLuan);
    
    if (existingLike.isPresent()) {
        // Nếu đã thích rồi và đang ở trạng thái đã hủy thích, cập nhật lại thành thích
        LuotThichBinhLuan luotThich = existingLike.get();
        if (!luotThich.getTrangThaiThich()) {
            luotThich.setTrangThaiThich(true);
            luotThich.setNgayHuyThich(null);
            luotThichBinhLuanRepository.save(luotThich);
            // Tự động cập nhật gợi ý kết bạn
            logger.info("[BinhLuan] Gọi gợi ý kết bạn cho userId: {}", idNguoiDung);
            goiYKetBanService.taoGoiYKetBan(idNguoiDung);
            if (!binhLuan.getNguoiDung().getId().equals(idNguoiDung)) {
                logger.info("[BinhLuan] Gọi gợi ý kết bạn cho chủ bình luận userId: {}", binhLuan.getNguoiDung().getId());
                goiYKetBanService.taoGoiYKetBan(binhLuan.getNguoiDung().getId());
            }
            // Ghi tương tác vào chi_tiet_tuong_tac
            ChiTietTuongTac chiTiet = new ChiTietTuongTac();
            chiTiet.setNguoi1(nguoiDung);
            chiTiet.setNguoi2(binhLuan.getNguoiDung());
            chiTiet.setLoaiTuongTac(LoaiTuongTac.like_binh_luan);
            chiTiet.setDiemTuongTac(3);
            chiTiet.setNgayTao(java.time.LocalDateTime.now());
            chiTietTuongTacRepository.save(chiTiet);
            return true;
        }
        // Nếu đã thích rồi và vẫn đang thích, không làm gì cả
        return false;
    } else {
        // Nếu chưa thích, tạo mới lượt thích
        LuotThichBinhLuan luotThich = new LuotThichBinhLuan();
        luotThich.setNguoiDung(nguoiDung);
        luotThich.setBinhLuan(binhLuan);
        luotThich.setTrangThaiThich(true);
        luotThichBinhLuanRepository.save(luotThich);
        // Tự động cập nhật gợi ý kết bạn
        logger.info("[BinhLuan] Gọi gợi ý kết bạn cho userId: {}", idNguoiDung);
        goiYKetBanService.taoGoiYKetBan(idNguoiDung);
        if (!binhLuan.getNguoiDung().getId().equals(idNguoiDung)) {
            logger.info("[BinhLuan] Gọi gợi ý kết bạn cho chủ bình luận userId: {}", binhLuan.getNguoiDung().getId());
            goiYKetBanService.taoGoiYKetBan(binhLuan.getNguoiDung().getId());
        }
        // Ghi tương tác vào chi_tiet_tuong_tac
        ChiTietTuongTac chiTiet = new ChiTietTuongTac();
        chiTiet.setNguoi1(nguoiDung);
        chiTiet.setNguoi2(binhLuan.getNguoiDung());
        chiTiet.setLoaiTuongTac(LoaiTuongTac.like_binh_luan);
        chiTiet.setDiemTuongTac(3);
        chiTiet.setNgayTao(java.time.LocalDateTime.now());
        chiTietTuongTacRepository.save(chiTiet);
        return true;
    }
}

    @Override
    @Transactional
    public boolean boThichBinhLuan(Integer idBinhLuan, Integer idNguoiDung) {
        // Tìm bình luận
        BinhLuan binhLuan = binhLuanRepository.findById(idBinhLuan)
                .orElseThrow(() -> new ResourceNotFoundException("Bình luận", "id", idBinhLuan));
        
        // Tìm người dùng
        NguoiDung nguoiDung = nguoiDungRepository.findById(idNguoiDung)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", idNguoiDung));
        
        // Kiểm tra xem người dùng đã thích bình luận này chưa
        Optional<LuotThichBinhLuan> existingLike = luotThichBinhLuanRepository.findByNguoiDungAndBinhLuan(nguoiDung, binhLuan);
        
        if (existingLike.isPresent() && existingLike.get().getTrangThaiThich()) {
            // Nếu đã thích, cập nhật thành bỏ thích
            LuotThichBinhLuan luotThich = existingLike.get();
            luotThich.setTrangThaiThich(false);
            luotThich.setNgayHuyThich(LocalDateTime.now());
            luotThichBinhLuanRepository.save(luotThich);
            // XÓA THÔNG BÁO TƯƠNG ỨNG
            thongBaoRepository.findByNguoiNhan(binhLuan.getNguoiDung()).stream()
                .filter(tb -> tb.getLoai().equals(LoaiThongBao.tuong_tac.name()) &&
                        tb.getNoiDung() != null &&
                        tb.getNoiDung().contains("Người dùng " + nguoiDung.getHoTen() + " vừa thích bình luận của bạn."))
                .forEach(tb -> thongBaoRepository.delete(tb));
            return true;
        }
        
        // Nếu chưa thích hoặc đã bỏ thích rồi, không làm gì cả
        return false;
    }

    /**
     * Chuyển đổi entity BinhLuan sang DTO
     * 
     * @param binhLuan Entity BinhLuan
     * @param idNguoiDungHienTai ID của người dùng hiện tại (để kiểm tra đã thích chưa)
     * @return BinhLuanDTO
     */
    private BinhLuanDTO convertToDTO(BinhLuan binhLuan, Integer idNguoiDungHienTai) {
        BinhLuanDTO dto = new BinhLuanDTO();
        dto.setId(binhLuan.getId());
        dto.setIdBaiViet(binhLuan.getBaiViet().getId());
        dto.setIdNguoiDung(binhLuan.getNguoiDung().getId());
        dto.setHoTenNguoiDung(binhLuan.getNguoiDung().getHoTen());
        
        // Lấy ảnh đại diện của người dùng nếu có
        binhLuan.getNguoiDung().getAnhDaiDien().stream()
                .filter(NguoiDungAnh::getLaAnhChinh)
                .findFirst()
                .ifPresent(anh -> dto.setAnhDaiDienNguoiDung(anh.getUrl()));
        
        // Lấy ID bình luận cha nếu có
        if (binhLuan.getBinhLuanCha() != null) {
            dto.setIdBinhLuanCha(binhLuan.getBinhLuanCha().getId());
        }
        
        dto.setNoiDung(binhLuan.getNoiDung());
        dto.setNgayTao(binhLuan.getNgayTao());
        dto.setNgayCapNhat(binhLuan.getNgayCapNhat());
        
        // Đếm số lượt thích
        long soLuotThich = luotThichBinhLuanRepository.countByBinhLuanAndTrangThaiThichTrue(binhLuan);
        dto.setSoLuotThich((int) soLuotThich);
        
        // Kiểm tra người dùng hiện tại đã thích bình luận chưa
        if (idNguoiDungHienTai != null) {
            boolean daThich = luotThichBinhLuanRepository.findByNguoiDungIdAndBinhLuanIdAndTrangThaiThichTrue(
                    idNguoiDungHienTai, binhLuan.getId()).isPresent();
            dto.setDaThich(daThich);
        } else {
            dto.setDaThich(false);
        }
        
        return dto;
    }
} 
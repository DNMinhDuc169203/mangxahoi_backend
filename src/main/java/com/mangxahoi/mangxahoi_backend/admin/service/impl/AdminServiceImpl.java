package com.mangxahoi.mangxahoi_backend.admin.service.impl;

import com.mangxahoi.mangxahoi_backend.admin.dto.request.DangNhapAdminRequest;
import com.mangxahoi.mangxahoi_backend.admin.dto.response.ThongKeResponse;
import com.mangxahoi.mangxahoi_backend.admin.service.AdminService;
import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import com.mangxahoi.mangxahoi_backend.dto.response.DangNhapResponse;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDungAnh;
import com.mangxahoi.mangxahoi_backend.entity.PhienDangNhapNguoiDung;
import com.mangxahoi.mangxahoi_backend.enums.VaiTro;
import com.mangxahoi.mangxahoi_backend.exception.AuthException;
import com.mangxahoi.mangxahoi_backend.exception.ResourceNotFoundException;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungAnhRepository;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import com.mangxahoi.mangxahoi_backend.repository.PhienDangNhapNguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.mangxahoi.mangxahoi_backend.admin.dto.request.ThemViPhamRequest;
import com.mangxahoi.mangxahoi_backend.admin.dto.response.LichSuViPhamDTO;
import com.mangxahoi.mangxahoi_backend.admin.dto.response.ThongTinViPhamNguoiDungDTO;
import com.mangxahoi.mangxahoi_backend.entity.LichSuViPham;
import com.mangxahoi.mangxahoi_backend.entity.BaoCao;
import com.mangxahoi.mangxahoi_backend.repository.LichSuViPhamRepository;
import com.mangxahoi.mangxahoi_backend.repository.BaoCaoRepository;
import com.mangxahoi.mangxahoi_backend.service.ThongBaoService;
import com.mangxahoi.mangxahoi_backend.repository.BaiVietRepository;
import com.mangxahoi.mangxahoi_backend.repository.BinhLuanRepository;
import com.mangxahoi.mangxahoi_backend.admin.dto.response.BaoCaoDTO;
import com.mangxahoi.mangxahoi_backend.enums.LoaiViPham;
import com.mangxahoi.mangxahoi_backend.entity.BaiViet;
import com.mangxahoi.mangxahoi_backend.entity.BinhLuan;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final NguoiDungRepository nguoiDungRepository;
    private final NguoiDungAnhRepository nguoiDungAnhRepository;
    private final PhienDangNhapNguoiDungRepository phienDangNhapRepository;
    private final PasswordEncoder passwordEncoder;
    private final LichSuViPhamRepository lichSuViPhamRepository;
    private final BaoCaoRepository baoCaoRepository;
    private final ThongBaoService thongBaoService;
    private final BaiVietRepository baiVietRepository;
    private final BinhLuanRepository binhLuanRepository;
    
    @Override
    public DangNhapResponse dangNhap(DangNhapAdminRequest request) {
        // Tìm người dùng theo email
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("Email không tồn tại", AuthException.EMAIL_SDT_KHONG_TON_TAI));

        // Kiểm tra mật khẩu
        boolean matKhauHopLe = false;
        
        // Kiểm tra nếu mật khẩu đã được mã hóa bằng BCrypt
        if (nguoiDung.getMatKhauHash() != null && nguoiDung.getMatKhauHash().startsWith("$2a$")) {
            matKhauHopLe = passwordEncoder.matches(request.getMatKhau(), nguoiDung.getMatKhauHash());
        } else {
            // Nếu mật khẩu chưa được mã hóa, so sánh trực tiếp
            matKhauHopLe = request.getMatKhau().equals(nguoiDung.getMatKhauHash());
        }
        
        if (!matKhauHopLe) {
            throw new AuthException("Mật khẩu không đúng", AuthException.MAT_KHAU_KHONG_DUNG);
        }

        // Kiểm tra vai trò - phải là admin
        if (nguoiDung.getVaiTro() != VaiTro.quan_tri_vien) {
            throw new AuthException("Tài khoản không có quyền quản trị", "NOT_ADMIN");
        }

        // Cập nhật thời gian đăng nhập
        nguoiDung.setLanDangNhapCuoi(LocalDateTime.now());
        nguoiDungRepository.save(nguoiDung);

        // Tạo phiên đăng nhập mới
        String token = UUID.randomUUID().toString();
        LocalDateTime hetHan = LocalDateTime.now().plusDays(1); // Phiên admin ngắn hơn phiên thường

        PhienDangNhapNguoiDung phien = PhienDangNhapNguoiDung.builder()
                .nguoiDung(nguoiDung)
                .maPhien(token)
                .hetHanLuc(hetHan)
                .vaiTro(VaiTro.quan_tri_vien)
                .build();

        phienDangNhapRepository.save(phien);

        // Trả về thông tin đăng nhập
        NguoiDungDTO nguoiDungDTO = chuyenSangDTO(nguoiDung);
        
        return DangNhapResponse.builder()
                .token(token)
                .nguoiDung(nguoiDungDTO)
                .build();
    }

    @Override
    public ThongKeResponse layThongKeTongQuat() {
        LocalDateTime startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();

        // Lấy tất cả bài viết trong tuần này
        List<BaiViet> baiVietsTuanNay = baiVietRepository.findByNgayTaoAfter(startOfWeek);

        // Đếm hashtag
        Map<String, Integer> hashtagCount = new HashMap<>();
        for (BaiViet bv : baiVietsTuanNay) {
            if (bv.getHashtags() != null) {
                for (var hashtag : bv.getHashtags()) {
                    String ten = hashtag.getTen().toLowerCase();
                    hashtagCount.put(ten, hashtagCount.getOrDefault(ten, 0) + 1);
                }
            }
        }

        // Tìm hashtag phổ biến nhất
        String hashtagPhoThong = "Chưa có dữ liệu";
        int soLanHashtagPhoThong = 0;
        if (!hashtagCount.isEmpty()) {
            var entry = hashtagCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get();
            hashtagPhoThong = entry.getKey();
            soLanHashtagPhoThong = entry.getValue();
        }

        return ThongKeResponse.builder()
                .tongSoNguoiDung(nguoiDungRepository.count())
                .nguoiDungMoi(nguoiDungRepository.countByNgayTaoAfter(startOfWeek))
                .tongSoBaiViet(baiVietRepository.count())
                .baiVietMoi(baiVietRepository.countByNgayTaoAfter(startOfWeek))
                .tongSoBinhLuan(binhLuanRepository.count())
                .binhLuanMoi(binhLuanRepository.countByNgayTaoAfter(startOfWeek))
                .tongSoBaoCao(baoCaoRepository.count())
                .baoCaoChuaXuLy(baoCaoRepository.countByTrangThai(com.mangxahoi.mangxahoi_backend.enums.TrangThaiBaoCao.cho_xu_ly))
                .nguoiDungBiKhoa(nguoiDungRepository.countByBiTamKhoaTrue())
                .trendTuanNay(new ThongKeResponse.ThongKeTrend(hashtagPhoThong, soLanHashtagPhoThong, 0, 0))
                .build();
    }

    @Override
    public Page<NguoiDungDTO> danhSachNguoiDung(Pageable pageable) {
        return nguoiDungRepository.findAll(pageable)
                .map(this::chuyenSangDTO);
    }

    @Override
    @Transactional
    public void khoaTaiKhoan(Integer id, String lyDo) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", id));
                
        // Không cho khóa tài khoản admin
        if (nguoiDung.getVaiTro() == VaiTro.quan_tri_vien) {
            throw new AuthException("Không thể khóa tài khoản quản trị viên", "ADMIN_CANNOT_BE_LOCKED");
        }

        nguoiDung.setBiTamKhoa(true);
        nguoiDung.setLyDoTamKhoa(lyDo);
        nguoiDung.setNgayTamKhoa(LocalDateTime.now());
        nguoiDungRepository.save(nguoiDung);
        
        // Đăng xuất người dùng bằng cách xóa tất cả phiên đăng nhập
        phienDangNhapRepository.deleteByNguoiDung(nguoiDung);
    }

    @Override
    @Transactional
    public void moKhoaTaiKhoan(Integer id) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", id));

        nguoiDung.setBiTamKhoa(false);
        nguoiDung.setLyDoTamKhoa(null);
        nguoiDung.setNgayMoKhoa(LocalDateTime.now());
        nguoiDungRepository.save(nguoiDung);
    }

    @Override
    public Page<BaoCaoDTO> danhSachBaoCao(String trangThai, Pageable pageable) {
        Page<BaoCao> page;
        if (trangThai != null && !trangThai.isEmpty()) {
            try {
                page = baoCaoRepository.findByTrangThai(
                    com.mangxahoi.mangxahoi_backend.enums.TrangThaiBaoCao.valueOf(trangThai), pageable);
            } catch (Exception e) {
                return Page.empty(); // hoặc có thể throw exception nếu muốn
            }
        } else {
            page = baoCaoRepository.findAll(pageable);
        }
        return page.map(bc -> {
            com.mangxahoi.mangxahoi_backend.admin.dto.response.BaoCaoDTO dto = new com.mangxahoi.mangxahoi_backend.admin.dto.response.BaoCaoDTO();
            dto.setId(bc.getId());
            dto.setLoaiBaoCao(bc.getLyDo() != null ? bc.getLyDo().toString() : null);
            dto.setNoiDung(bc.getMoTa());
            dto.setNgayTao(bc.getNgayTao());
            dto.setTenNguoiBaoCao(bc.getNguoiBaoCao() != null ? bc.getNguoiBaoCao().getHoTen() : null);
            if (bc.getBaiViet() != null) {
                dto.setLoaiDoiTuongBiBaoCao("BÀI VIẾT");
                dto.setTenNguoiBiBaoCao(bc.getBaiViet().getNguoiDung() != null ? bc.getBaiViet().getNguoiDung().getHoTen() : null);
                dto.setNoiDungDoiTuongBiBaoCao(bc.getBaiViet().getNoiDung());
            } else if (bc.getBinhLuan() != null) {
                dto.setLoaiDoiTuongBiBaoCao("BÌNH LUẬN");
                dto.setTenNguoiBiBaoCao(bc.getBinhLuan().getNguoiDung() != null ? bc.getBinhLuan().getNguoiDung().getHoTen() : null);
                dto.setNoiDungDoiTuongBiBaoCao(bc.getBinhLuan().getNoiDung());
            } else if (bc.getNguoiDungBiBaoCao() != null) {
                dto.setLoaiDoiTuongBiBaoCao("NGƯỜI DÙNG");
                dto.setTenNguoiBiBaoCao(bc.getNguoiDungBiBaoCao().getHoTen());
                dto.setNoiDungDoiTuongBiBaoCao(null);
            } else {
                dto.setLoaiDoiTuongBiBaoCao("KHÔNG XÁC ĐỊNH");
                dto.setTenNguoiBiBaoCao(null);
                dto.setNoiDungDoiTuongBiBaoCao(null);
            }
            dto.setTrangThai(bc.getTrangThai() != null ? bc.getTrangThai().toString() : null);
            dto.setNgayGui(bc.getNgayTao());
            return dto;
        });
    }

    @Override
    @Transactional
    public void xuLyBaoCao(Integer id, String trangThai, String ghiChu) {
        BaoCao baoCao = baoCaoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy báo cáo với id: " + id));
        baoCao.setTrangThai(com.mangxahoi.mangxahoi_backend.enums.TrangThaiBaoCao.valueOf(trangThai));
        baoCao.setGhiChuXuLy(ghiChu);
        baoCao.setNgayXuLy(java.time.LocalDateTime.now());
        baoCaoRepository.save(baoCao);

        // Gửi thông báo và xử lý đối tượng
        try {
            if ("da_xu_ly".equals(trangThai)) {
                // Gửi thông báo cho người báo cáo
                if (baoCao.getNguoiBaoCao() != null) {
                    thongBaoService.guiThongBaoHeThong(
                        baoCao.getNguoiBaoCao().getId(),
                        "Báo cáo đã được xử lý",
                        "Cảm ơn bạn đã báo cáo. Báo cáo của bạn đã được xử lý!"
                    );
                }
                // Xử lý đối tượng bị báo cáo
                if (baoCao.getBaiViet() != null) {
                    // Ẩn bài viết
                    BaiViet baiViet = baoCao.getBaiViet();
                    baiViet.setBiAn(true);
                    baiVietRepository.save(baiViet);
                    // Thông báo cho chủ bài viết
                    if (baiViet.getNguoiDung() != null) {
                        thongBaoService.guiThongBaoHeThong(
                            baiViet.getNguoiDung().getId(),
                            "Bài viết bị ẩn",
                            "Bài viết của bạn đã bị ẩn do vi phạm tiêu chuẩn cộng đồng."
                        );
                    }
                } else if (baoCao.getBinhLuan() != null) {
                    // Ẩn bình luận
                    BinhLuan binhLuan = baoCao.getBinhLuan();
                    binhLuan.setBiAn(true);
                    binhLuanRepository.save(binhLuan);
                    // Thông báo cho chủ bình luận
                    if (binhLuan.getNguoiDung() != null) {
                        thongBaoService.guiThongBaoHeThong(
                            binhLuan.getNguoiDung().getId(),
                            "Bình luận bị ẩn",
                            "Bình luận của bạn đã bị ẩn do vi phạm tiêu chuẩn cộng đồng."
                        );
                    }
                } else if (baoCao.getNguoiDungBiBaoCao() != null) {
                    // Cảnh báo người bị báo cáo
                    NguoiDung nguoiBiBaoCao = baoCao.getNguoiDungBiBaoCao();
                    thongBaoService.guiThongBaoHeThong(
                        nguoiBiBaoCao.getId(),
                        "Bạn bị cảnh báo",
                        "Bạn đã bị cảnh báo do vi phạm tiêu chuẩn cộng đồng."
                    );
                }
            } else if ("tu_choi".equals(trangThai)) {
                // Gửi thông báo cho người báo cáo
                if (baoCao.getNguoiBaoCao() != null) {
                    thongBaoService.guiThongBaoHeThong(
                        baoCao.getNguoiBaoCao().getId(),
                        "Báo cáo bị từ chối",
                        "Báo cáo của bạn đã bị từ chối. Nếu có thắc mắc, vui lòng liên hệ admin."
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi gửi thông báo hoặc xử lý đối tượng báo cáo: " + e.getMessage());
        }
    }
    
    @Override
    public List<LichSuViPhamDTO> lichSuViPhamNguoiDung(Integer userId) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", userId));
        List<LichSuViPham> lichSu = lichSuViPhamRepository.findByNguoiDung(nguoiDung);
        return lichSu.stream().map(this::toLichSuViPhamDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LichSuViPhamDTO themViPhamNguoiDung(ThemViPhamRequest request, Integer adminId) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", request.getUserId()));
        NguoiDung admin = nguoiDungRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", adminId));
        BaoCao baoCao = null;
        if (request.getBaoCaoId() != null) {
            baoCao = baoCaoRepository.findById(request.getBaoCaoId()).orElse(null);
        }
        // Validate loại vi phạm
        LoaiViPham loaiViPham = request.getLoaiViPham();
        if (loaiViPham == null) {
            throw new IllegalArgumentException("Loại vi phạm không hợp lệ hoặc không được để trống!");
        }
        // Đếm số lần vi phạm trước đó
        long soLanViPham = lichSuViPhamRepository.countByNguoiDung(nguoiDung);
        // Xác định hình phạt
        String hinhPhat;
        if (soLanViPham == 0) {
            hinhPhat = "Cảnh báo";
        } else if (soLanViPham == 1) {
            hinhPhat = "Khóa 1 ngày";
        } else if (soLanViPham == 2) {
            hinhPhat = "Khóa 3 ngày";
        } else {
            hinhPhat = "Khóa vĩnh viễn";
        }
        // Áp dụng hình phạt lên tài khoản
        if (hinhPhat.equals("Khóa 1 ngày")) {
            nguoiDung.setBiTamKhoa(true);
            nguoiDung.setLyDoTamKhoa(request.getNoiDungViPham());
            nguoiDung.setNgayTamKhoa(java.time.LocalDateTime.now());
            nguoiDung.setNgayMoKhoa(java.time.LocalDateTime.now().plusDays(1));
        } else if (hinhPhat.equals("Khóa 3 ngày")) {
            nguoiDung.setBiTamKhoa(true);
            nguoiDung.setLyDoTamKhoa(request.getNoiDungViPham());
            nguoiDung.setNgayTamKhoa(java.time.LocalDateTime.now());
            nguoiDung.setNgayMoKhoa(java.time.LocalDateTime.now().plusDays(3));
        } else if (hinhPhat.equals("Khóa vĩnh viễn")) {
            nguoiDung.setBiTamKhoa(true);
            nguoiDung.setLyDoTamKhoa(request.getNoiDungViPham());
            nguoiDung.setNgayTamKhoa(java.time.LocalDateTime.now());
            nguoiDung.setNgayMoKhoa(null);
        }
        nguoiDungRepository.save(nguoiDung);
        // Lưu lịch sử vi phạm
        LichSuViPham viPham = new LichSuViPham();
        viPham.setNguoiDung(nguoiDung);
        viPham.setNoiDungViPham(request.getNoiDungViPham());
        viPham.setLoaiViPham(loaiViPham);
        viPham.setThoiGianViPham(java.time.LocalDateTime.now());
        viPham.setHinhPhat(hinhPhat);
        viPham.setTrangThaiXuLy("Đã xử lý");
        viPham.setAdminXuLy(admin);
        viPham.setGhiChu(request.getGhiChu());
        viPham.setBaoCaoLienQuan(baoCao);
        lichSuViPhamRepository.save(viPham);
        // Gửi thông báo cho user
        try {
            thongBaoService.guiThongBaoHeThong(
                nguoiDung.getId(),
                "Xử lý vi phạm",
                "Bạn đã bị xử lý vi phạm: " + hinhPhat + ". Lý do: " + request.getNoiDungViPham()
            );
        } catch (Exception e) {
            System.err.println("Lỗi gửi thông báo vi phạm: " + e.getMessage());
        }
        return toLichSuViPhamDTO(viPham);
    }

    @Override
    public ThongTinViPhamNguoiDungDTO thongTinViPhamNguoiDung(Integer userId) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", userId));
        long soLanViPham = lichSuViPhamRepository.countByNguoiDung(nguoiDung);
        LichSuViPham viPhamGanNhat = lichSuViPhamRepository.findByNguoiDung(nguoiDung)
                .stream().reduce((first, second) -> second).orElse(null);
        ThongTinViPhamNguoiDungDTO dto = new ThongTinViPhamNguoiDungDTO();
        dto.setUserId(nguoiDung.getId());
        dto.setTenNguoiDung(nguoiDung.getHoTen());
        dto.setTongSoLanViPham((int) soLanViPham);
        dto.setHinhPhatGanNhat(viPhamGanNhat != null ? viPhamGanNhat.getHinhPhat() : null);
        dto.setTrangThaiTaiKhoan(
            nguoiDung.getBiXoaMem() ? "Đã xóa" : (nguoiDung.getBiTamKhoa() ? "Đang bị khóa" : "Bình thường")
        );
        return dto;
    }

    @Override
    public List<BaoCaoDTO> findTop5BaoCaoMoiNhat() {
        List<BaoCao> list = baoCaoRepository.findTop5ByOrderByNgayTaoDesc(PageRequest.of(0, 5));
        return list.stream().map(bc -> {
            BaoCaoDTO dto = new BaoCaoDTO();
            dto.setId(bc.getId());
            dto.setLoaiBaoCao(bc.getLyDo() != null ? bc.getLyDo().toString() : null);
            dto.setNoiDung(bc.getMoTa());
            dto.setNgayTao(bc.getNgayTao());
            dto.setTenNguoiBaoCao(bc.getNguoiBaoCao() != null ? bc.getNguoiBaoCao().getHoTen() : null);
            dto.setTenNguoiBiBaoCao(bc.getNguoiDungBiBaoCao() != null ? bc.getNguoiDungBiBaoCao().getHoTen() : null);
            return dto;
        }).toList();
    }

    @Override
    public void logout(String token) {
        phienDangNhapRepository.deleteByMaPhien(token);
    }

    private NguoiDungDTO chuyenSangDTO(NguoiDung nguoiDung) {
        // Lấy ảnh đại diện
        String anhDaiDien = null;
        Optional<NguoiDungAnh> anhDaiDienOpt = nguoiDungAnhRepository.findByNguoiDungAndLaAnhChinh(nguoiDung, true);
        if (anhDaiDienOpt.isPresent()) {
            anhDaiDien = anhDaiDienOpt.get().getUrl();
        }
        
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

    private LichSuViPhamDTO toLichSuViPhamDTO(LichSuViPham v) {
        LichSuViPhamDTO dto = new LichSuViPhamDTO();
        dto.setId(v.getId());
        dto.setUserId(v.getNguoiDung().getId());
        dto.setTenNguoiDung(v.getNguoiDung().getHoTen());
        dto.setNoiDungViPham(v.getNoiDungViPham());
        dto.setLoaiViPham(v.getLoaiViPham());
        dto.setThoiGianViPham(v.getThoiGianViPham());
        dto.setHinhPhat(v.getHinhPhat());
        dto.setTrangThaiXuLy(v.getTrangThaiXuLy());
        dto.setAdminXuLyId(v.getAdminXuLy() != null ? v.getAdminXuLy().getId() : null);
        dto.setTenAdminXuLy(v.getAdminXuLy() != null ? v.getAdminXuLy().getHoTen() : null);
        dto.setGhiChu(v.getGhiChu());
        dto.setBaoCaoId(v.getBaoCaoLienQuan() != null ? v.getBaoCaoLienQuan().getId() : null);
        return dto;
    }
}
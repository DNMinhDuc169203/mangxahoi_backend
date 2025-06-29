package com.mangxahoi.mangxahoi_backend.service.impl;

import com.mangxahoi.mangxahoi_backend.dto.request.DangNhapRequest;
import com.mangxahoi.mangxahoi_backend.dto.response.DangNhapResponse;
import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDungAnh;
import com.mangxahoi.mangxahoi_backend.entity.PhienDangNhapNguoiDung;
import com.mangxahoi.mangxahoi_backend.enums.LoaiMaXacThuc;
import com.mangxahoi.mangxahoi_backend.exception.AuthException;
import com.mangxahoi.mangxahoi_backend.exception.ResourceNotFoundException;
import com.mangxahoi.mangxahoi_backend.exception.ValidationException;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungAnhRepository;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import com.mangxahoi.mangxahoi_backend.repository.PhienDangNhapNguoiDungRepository;
import com.mangxahoi.mangxahoi_backend.service.CloudinaryService;
import com.mangxahoi.mangxahoi_backend.service.NguoiDungService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NguoiDungServiceImpl implements NguoiDungService {

    private final NguoiDungRepository nguoiDungRepository;
    private final NguoiDungAnhRepository nguoiDungAnhRepository;
    private final PhienDangNhapNguoiDungRepository phienDangNhapRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public NguoiDungDTO dangKy(NguoiDungDTO nguoiDungDTO, String matKhau) {
        // Kiểm tra dữ liệu đầu vào
        List<String> validationErrors = new ArrayList<>();
        if (nguoiDungDTO.getEmail() == null || nguoiDungDTO.getEmail().isEmpty()) {
            validationErrors.add("Email không được để trống");
        }
        if (nguoiDungDTO.getSoDienThoai() == null || nguoiDungDTO.getSoDienThoai().isEmpty()) {
            validationErrors.add("Số điện thoại không được để trống");
        }
        if (matKhau == null || matKhau.isEmpty()) {
            validationErrors.add("Mật khẩu không được để trống");
        } else if (matKhau.length() < 6) {
            validationErrors.add("Mật khẩu phải có ít nhất 6 ký tự");
        }
        if (nguoiDungDTO.getHoTen() == null || nguoiDungDTO.getHoTen().isEmpty()) {
            validationErrors.add("Họ tên không được để trống");
        }
        
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Dữ liệu đăng ký không hợp lệ", validationErrors);
        }
        
        // Kiểm tra email hoặc số điện thoại đã tồn tại chưa
        if (nguoiDungRepository.existsByEmail(nguoiDungDTO.getEmail())) {
            throw new AuthException("Email đã được sử dụng", AuthException.EMAIL_DA_TON_TAI);
        }
        
        if (nguoiDungRepository.existsBySoDienThoai(nguoiDungDTO.getSoDienThoai())) {
            throw new AuthException("Số điện thoại đã được sử dụng", AuthException.SDT_DA_TON_TAI);
        }
        
        // Tạo mã xác thực ngẫu nhiên 6 số
        String maXacThuc = taoMaNgauNhien();
        
        // Tạo người dùng mới
        NguoiDung nguoiDung = NguoiDung.builder()
                .email(nguoiDungDTO.getEmail())
                .soDienThoai(nguoiDungDTO.getSoDienThoai())
                .matKhauHash(passwordEncoder.encode(matKhau))
                .hoTen(nguoiDungDTO.getHoTen())
                .tieuSu(nguoiDungDTO.getTieuSu())
                .ngaySinh(nguoiDungDTO.getNgaySinh())
                .gioiTinh(nguoiDungDTO.getGioiTinh())
                .diaChi(nguoiDungDTO.getDiaChi())
                .mucRiengTu(nguoiDungDTO.getMucRiengTu())
                .tokenXacThuc(maXacThuc)
                .loaiMaXacThuc(LoaiMaXacThuc.xac_thuc_tai_khoan)
                .build();
        
        NguoiDung savedNguoiDung = nguoiDungRepository.save(nguoiDung);
        
        // Nếu có ảnh đại diện, lưu ảnh
        // if (nguoiDungDTO.getAnhDaiDien() != null && !nguoiDungDTO.getAnhDaiDien().isEmpty()) {
        //     NguoiDungAnh anhDaiDien = NguoiDungAnh.builder()
        //             .nguoiDung(savedNguoiDung)
        //             .url(nguoiDungDTO.getAnhDaiDien())
        //             .laAnhChinh(true)
        //             .build();
        //     nguoiDungAnhRepository.save(anhDaiDien);
        // }
        
        return chuyenSangDTO(savedNguoiDung);
    }
    
    @Override
    @Transactional
    public DangNhapResponse dangNhap(DangNhapRequest request) {
        // Kiểm tra dữ liệu đầu vào
        if (request.getEmailHoacSoDienThoai() == null || request.getEmailHoacSoDienThoai().isEmpty()) {
            throw new ValidationException("Email hoặc số điện thoại không được để trống");
        }
        if (request.getMatKhau() == null || request.getMatKhau().isEmpty()) {
            throw new ValidationException("Mật khẩu không được để trống");
        }
        
        // Tìm người dùng theo email hoặc số điện thoại
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(request.getEmailHoacSoDienThoai())
                .orElseGet(() -> nguoiDungRepository.findBySoDienThoai(request.getEmailHoacSoDienThoai())
                        .orElseThrow(() -> new AuthException(
                            "Email hoặc số điện thoại không tồn tại", 
                            AuthException.EMAIL_SDT_KHONG_TON_TAI
                        )));
        
        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(request.getMatKhau(), nguoiDung.getMatKhauHash())) {
            throw new AuthException("Mật khẩu không đúng", AuthException.MAT_KHAU_KHONG_DUNG);
        }
        
        // Kiểm tra trạng thái tài khoản
        if (nguoiDung.getBiTamKhoa()) {
            throw new AuthException(
                "Tài khoản đã bị tạm khóa" + (nguoiDung.getLyDoTamKhoa() != null ? ": " + nguoiDung.getLyDoTamKhoa() : ""), 
                AuthException.TAI_KHOAN_BI_KHOA
            );
        }
        
        if (nguoiDung.getBiXoaMem()) {
            throw new AuthException("Tài khoản đã bị xóa", AuthException.TAI_KHOAN_BI_XOA);
        }
        
        // Cập nhật thời gian đăng nhập cuối
        nguoiDung.setLanDangNhapCuoi(LocalDateTime.now());
        nguoiDung.setDangHoatDong(true);
        nguoiDungRepository.save(nguoiDung);
        
        // Tạo phiên đăng nhập mới
        String token = UUID.randomUUID().toString();
        LocalDateTime hetHan = LocalDateTime.now().plusDays(30); // Phiên hết hạn sau 30 ngày
        
        PhienDangNhapNguoiDung phien = PhienDangNhapNguoiDung.builder()
                .nguoiDung(nguoiDung)
                .maPhien(token)
                .hetHanLuc(hetHan)
                .vaiTro(nguoiDung.getVaiTro())
                .build();
        
        phienDangNhapRepository.save(phien);
        
        // Trả về thông tin đăng nhập
        return DangNhapResponse.builder()
                .token(token)
                .nguoiDung(chuyenSangDTO(nguoiDung))
                .build();
    }

    @Override
    @Transactional
    public NguoiDungDTO capNhatThongTin(Integer id, NguoiDungDTO nguoiDungDTO) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", id));
        
        // Cập nhật thông tin
        nguoiDung.setHoTen(nguoiDungDTO.getHoTen());
        nguoiDung.setTieuSu(nguoiDungDTO.getTieuSu());
        nguoiDung.setNgaySinh(nguoiDungDTO.getNgaySinh());
        nguoiDung.setGioiTinh(nguoiDungDTO.getGioiTinh());
        nguoiDung.setDiaChi(nguoiDungDTO.getDiaChi());
        nguoiDung.setMucRiengTu(nguoiDungDTO.getMucRiengTu());
        
        NguoiDung updatedNguoiDung = nguoiDungRepository.save(nguoiDung);
        
        // Cập nhật ảnh đại diện nếu có
        if (nguoiDungDTO.getAnhDaiDien() != null && !nguoiDungDTO.getAnhDaiDien().isEmpty()) {
            // Tìm ảnh đại diện cũ
            Optional<NguoiDungAnh> anhDaiDienCu = nguoiDungAnhRepository.findByNguoiDungAndLaAnhChinh(updatedNguoiDung, true);
            
            if (anhDaiDienCu.isPresent()) {
                // Cập nhật ảnh đại diện cũ
                NguoiDungAnh anh = anhDaiDienCu.get();
                anh.setUrl(nguoiDungDTO.getAnhDaiDien());
                nguoiDungAnhRepository.save(anh);
            } else {
                // Tạo ảnh đại diện mới
                NguoiDungAnh anhDaiDien = NguoiDungAnh.builder()
                        .nguoiDung(updatedNguoiDung)
                        .url(nguoiDungDTO.getAnhDaiDien())
                        .laAnhChinh(true)
                        .build();
                nguoiDungAnhRepository.save(anhDaiDien);
            }
        }
        
        return chuyenSangDTO(updatedNguoiDung);
    }

    @Override
    @Transactional
    public void xoaNguoiDung(Integer id) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", id));
        
        // Xóa mềm
        nguoiDung.setBiXoaMem(true);
        nguoiDung.setNgayXoaMem(LocalDateTime.now());
        nguoiDungRepository.save(nguoiDung);
    }

    @Override
    public Optional<NguoiDungDTO> timTheoId(Integer id) {
        return nguoiDungRepository.findById(id)
                .map(this::chuyenSangDTO);
    }

    @Override
    public Optional<NguoiDungDTO> timTheoEmail(String email) {
        return nguoiDungRepository.findByEmail(email)
                .map(this::chuyenSangDTO);
    }

    @Override
    public Optional<NguoiDungDTO> timTheoSoDienThoai(String soDienThoai) {
        return nguoiDungRepository.findBySoDienThoai(soDienThoai)
                .map(this::chuyenSangDTO);
    }

    @Override
    public Page<NguoiDungDTO> timTatCa(Pageable pageable) {
        return nguoiDungRepository.findAll(pageable)
                .map(this::chuyenSangDTO);
    }

    @Override
    public Page<NguoiDungDTO> timTheoHoTen(String hoTen, Pageable pageable) {
        // Cần thêm phương thức trong repository
        // Giả sử đã có phương thức này
        return null;
    }

    @Override
    @Transactional
    public boolean xacThucEmail(String email, String maXacThuc) {
        if (email == null || email.isEmpty()) {
            throw new ValidationException("Email không được để trống");
        }
        if (maXacThuc == null || maXacThuc.isEmpty()) {
            throw new ValidationException("Mã xác thực không được để trống");
        }
        
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "email", email));
        
        // Kiểm tra nếu người dùng đã xác thực
        if (nguoiDung.getDaXacThuc() != null && nguoiDung.getDaXacThuc()) {
            throw new AuthException("Tài khoản đã được xác thực trước đó", AuthException.XAC_THUC_THAT_BAI);
        }
        
        // Kiểm tra mã xác thực
        if (nguoiDung.getTokenXacThuc() == null || !nguoiDung.getTokenXacThuc().equals(maXacThuc)) {
            throw new AuthException("Mã xác thực không hợp lệ hoặc đã hết hạn", AuthException.TOKEN_KHONG_HOP_LE);
        }
        
        if (nguoiDung.getLoaiMaXacThuc() != LoaiMaXacThuc.xac_thuc_tai_khoan) {
            throw new AuthException("Mã xác thực không phải dùng cho việc xác thực tài khoản", "AUTH_008");
        }
        
        // Xác thực tài khoản
        nguoiDung.setDaXacThuc(true);
        nguoiDung.setTokenXacThuc(null);
        nguoiDung.setLoaiMaXacThuc(null);
        nguoiDungRepository.save(nguoiDung);
        
        return true;
    }

    @Override
    @Transactional
    public boolean datLaiMatKhau(String email, String matKhauMoi) {
        if (email == null || email.isEmpty()) {
            throw new ValidationException("Email không được để trống");
        }
        if (matKhauMoi == null || matKhauMoi.isEmpty()) {
            throw new ValidationException("Mật khẩu mới không được để trống");
        }
        if (matKhauMoi.length() < 6) {
            throw new ValidationException("Mật khẩu phải có ít nhất 6 ký tự");
        }
        
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "email", email));
        
        nguoiDung.setMatKhauHash(passwordEncoder.encode(matKhauMoi));
        nguoiDungRepository.save(nguoiDung);
        
        return true;
    }
    
    @Override
    @Transactional
    public String uploadAnhDaiDien(Integer id, MultipartFile file, boolean laAnhChinh) throws IOException {
        // Kiểm tra người dùng tồn tại
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", id));
        
        // Upload ảnh lên Cloudinary
        String imageUrl = cloudinaryService.uploadFile(file, "nguoi_dung_anh");
        
        // Nếu là ảnh chính, cập nhật các ảnh khác thành không phải ảnh chính
        if (laAnhChinh) {
            List<NguoiDungAnh> anhDaiDiens = nguoiDungAnhRepository.findByNguoiDung(nguoiDung);
            for (NguoiDungAnh anh : anhDaiDiens) {
                if (anh.getLaAnhChinh()) {
                    anh.setLaAnhChinh(false);
                    nguoiDungAnhRepository.save(anh);
                }
            }
        }
        
        // Lưu thông tin ảnh vào database
        NguoiDungAnh anhDaiDien = NguoiDungAnh.builder()
                .nguoiDung(nguoiDung)
                .url(imageUrl)
                .laAnhChinh(laAnhChinh)
                .build();
        
        nguoiDungAnhRepository.save(anhDaiDien);
        
        return imageUrl;
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

    /**
     * Tạo mã xác thực ngẫu nhiên 6 số
     */
    private String taoMaNgauNhien() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000); // số từ 100000 đến 999999
        return String.valueOf(number);
    }
}
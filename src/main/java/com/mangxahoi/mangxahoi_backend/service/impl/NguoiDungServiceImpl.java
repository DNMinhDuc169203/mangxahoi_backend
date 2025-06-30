package com.mangxahoi.mangxahoi_backend.service.impl;

import com.mangxahoi.mangxahoi_backend.dto.request.DangNhapRequest;
import com.mangxahoi.mangxahoi_backend.dto.response.DangNhapResponse;
import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import com.mangxahoi.mangxahoi_backend.dto.NguoiDungAnhDTO;
import com.mangxahoi.mangxahoi_backend.dto.request.PrivacySettingsRequest;
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
import com.mangxahoi.mangxahoi_backend.repository.KetBanRepository;
import com.mangxahoi.mangxahoi_backend.service.CloudinaryService;
import com.mangxahoi.mangxahoi_backend.service.NguoiDungService;
import com.mangxahoi.mangxahoi_backend.util.TokenUtil;
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
import java.util.Comparator;
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
    private final KetBanRepository ketBanRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final TokenUtil tokenUtil;

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
    public Optional<NguoiDungDTO> timTheoId(Integer profileId, String requesterToken) {
        // Lấy thông tin người dùng của profile được yêu cầu
        Optional<NguoiDung> profileOwnerOpt = nguoiDungRepository.findById(profileId);
        if (profileOwnerOpt.isEmpty()) {
            return Optional.empty();
        }
        NguoiDung profileOwner = profileOwnerOpt.get();

        // Lấy thông tin người dùng đang thực hiện yêu cầu (nếu có)
        NguoiDung requester = null;
        if (requesterToken != null && !requesterToken.isEmpty()) {
            try {
                requester = tokenUtil.layNguoiDungTuToken(requesterToken);
            } catch (Exception e) {
                // Bỏ qua nếu token không hợp lệ, coi như người dùng chưa đăng nhập
            }
        }

        // Nếu người xem là chính chủ hồ sơ hoặc là admin, luôn trả về đầy đủ
        if (requester != null && (requester.getId().equals(profileOwner.getId()) || requester.getVaiTro() == com.mangxahoi.mangxahoi_backend.enums.VaiTro.quan_tri_vien)) {
            return Optional.of(chuyenSangDTO(profileOwner));
        }

        // Áp dụng logic dựa trên mức riêng tư
        switch (profileOwner.getMucRiengTu()) {
            case cong_khai:
                return Optional.of(chuyenSangDTO(profileOwner));

            case rieng_tu:
                // Chỉ admin hoặc chính chủ mới được xem (đã xử lý ở trên)
                // Những người khác sẽ thấy thông tin hạn chế
                return Optional.of(chuyenSangDTOHancHe(profileOwner));

            case ban_be:
                if (requester != null && ketBanRepository.areFriends(profileOwner, requester).isPresent()) {
                    return Optional.of(chuyenSangDTO(profileOwner));
                } else {
                    return Optional.of(chuyenSangDTOHancHe(profileOwner));
                }

            default:
                return Optional.empty(); // Hoặc trả về hạn chế theo mặc định
        }
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
        return nguoiDungRepository.findByHoTenContainingIgnoreCase(hoTen, pageable)
                .map(this::chuyenSangDTO);
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
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", id));

        // Nếu là ảnh chính, tìm và xóa ảnh đại diện chính cũ
        if (laAnhChinh) {
            nguoiDungAnhRepository.findByNguoiDungAndLaAnhChinh(nguoiDung, true).ifPresent(anhCu -> {
                try {
                    // Xóa ảnh cũ trên Cloudinary
                    String oldImageUrl = anhCu.getUrl();
                    String publicId = extractPublicIdFromUrl(oldImageUrl);
                    cloudinaryService.deleteFile(publicId);

                    // Xóa ảnh cũ trong DB
                    nguoiDungAnhRepository.delete(anhCu);
                } catch (IOException e) {
                    // Cần có cơ chế xử lý lỗi tốt hơn ở đây, ví dụ như logging
                    // Tạm thời ném ra ngoài để controller xử lý
                    throw new RuntimeException("Lỗi khi xóa ảnh cũ: " + e.getMessage(), e);
                }
            });
        }
        
        // Upload ảnh mới lên Cloudinary
        String imageUrl = cloudinaryService.uploadFile(file, "nguoi_dung_anh");
        
        // Lưu thông tin ảnh mới vào database
        NguoiDungAnh anhDaiDien = NguoiDungAnh.builder()
                .nguoiDung(nguoiDung)
                .url(imageUrl)
                .laAnhChinh(laAnhChinh)
                .build();
        
        nguoiDungAnhRepository.save(anhDaiDien);
        
        return imageUrl;
    }
    
    @Override
    public NguoiDungDTO layThongTinHienTai(String token) {
        // Lấy thông tin người dùng từ token
        NguoiDung nguoiDung = tokenUtil.layNguoiDungTuToken(token);
        
        // Chuyển đổi sang DTO và trả về
        return chuyenSangDTO(nguoiDung);
    }
    
    private NguoiDungDTO chuyenSangDTO(NguoiDung nguoiDung) {
        if (nguoiDung == null) {
            return null;
        }
        NguoiDungDTO dto = new NguoiDungDTO();
        dto.setId(nguoiDung.getId());
        dto.setEmail(nguoiDung.getEmail());
        dto.setSoDienThoai(nguoiDung.getSoDienThoai());
        dto.setHoTen(nguoiDung.getHoTen());
        dto.setTieuSu(nguoiDung.getTieuSu());
        dto.setNgaySinh(nguoiDung.getNgaySinh());
        dto.setGioiTinh(nguoiDung.getGioiTinh());
        dto.setDiaChi(nguoiDung.getDiaChi());
        dto.setMucRiengTu(nguoiDung.getMucRiengTu());
        dto.setSoBanBe(nguoiDung.getSoBanBe());
        dto.setSoBaiDang(nguoiDung.getSoBaiDang());

        dto.setEmailCongKhai(nguoiDung.getEmailCongKhai());
        dto.setSdtCongKhai(nguoiDung.getSdtCongKhai());
        dto.setNgaySinhCongKhai(nguoiDung.getNgaySinhCongKhai());
        dto.setGioiTinhCongKhai(nguoiDung.getGioiTinhCongKhai());

        Optional<NguoiDungAnh> anhChinhOpt = nguoiDungAnhRepository.findByNguoiDungAndLaAnhChinh(nguoiDung, true);
        anhChinhOpt.ifPresent(nguoiDungAnh -> dto.setAnhDaiDien(nguoiDungAnh.getUrl()));

        return dto;
    }

    /**
     * Chuyển đổi NguoiDung sang NguoiDungDTO với thông tin bị hạn chế
     */
    private NguoiDungDTO chuyenSangDTOHancHe(NguoiDung nguoiDung) {
        // Lấy ảnh đại diện
        String anhDaiDien = null;
        Optional<NguoiDungAnh> anhDaiDienOpt = nguoiDungAnhRepository.findByNguoiDungAndLaAnhChinh(nguoiDung, true);
        if (anhDaiDienOpt.isPresent()) {
            anhDaiDien = anhDaiDienOpt.get().getUrl();
        }

        return NguoiDungDTO.builder()
                .id(nguoiDung.getId())
                .hoTen(nguoiDung.getHoTen())
                .tieuSu(nguoiDung.getTieuSu())
                .anhDaiDien(anhDaiDien)
                // Các trường khác sẽ là null hoặc giá trị mặc định
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

    @Override
    public boolean doiMatKhau(String token, String matKhauCu, String matKhauMoi) {
        NguoiDung nguoiDung = tokenUtil.layNguoiDungTuToken(token);
        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(matKhauCu, nguoiDung.getMatKhauHash())) {
            throw new ValidationException("Mật khẩu cũ không đúng");
        }
        // Kiểm tra mật khẩu mới khác mật khẩu cũ
        if (passwordEncoder.matches(matKhauMoi, nguoiDung.getMatKhauHash())) {
            throw new ValidationException("Mật khẩu mới không được trùng với mật khẩu cũ");
        }
        // Đổi mật khẩu
        nguoiDung.setMatKhauHash(passwordEncoder.encode(matKhauMoi));
        nguoiDungRepository.save(nguoiDung);
        return true;
    }

    @Override
    @Transactional
    public void xoaAnhDaiDien(Integer nguoiDungId, Integer anhId) throws IOException {
        // Tìm ảnh trong DB
        NguoiDungAnh anh = nguoiDungAnhRepository.findById(anhId)
                .orElseThrow(() -> new ResourceNotFoundException("Ảnh", "id", anhId));

        // Kiểm tra ảnh có thuộc về người dùng không
        if (!anh.getNguoiDung().getId().equals(nguoiDungId)) {
            throw new AuthException("Bạn không có quyền xóa ảnh này", "AUTH_FORBIDDEN");
        }

        // Xóa ảnh khỏi Cloudinary
        String imageUrl = anh.getUrl();
        String publicId = extractPublicIdFromUrl(imageUrl);
        cloudinaryService.deleteFile(publicId);

        // Xóa ảnh khỏi database
        nguoiDungAnhRepository.delete(anh);

        // Nếu ảnh vừa xóa là ảnh chính, và người dùng vẫn còn ảnh khác,
        // thì đặt ảnh mới nhất làm ảnh chính mới.
        if (anh.getLaAnhChinh()) {
            NguoiDung nguoiDung = anh.getNguoiDung();
            List<NguoiDungAnh> remainingAvatars = nguoiDungAnhRepository.findByNguoiDung(nguoiDung);
            if (!remainingAvatars.isEmpty()) {
                // Sắp xếp để tìm ảnh mới nhất
                remainingAvatars.sort(Comparator.comparing(NguoiDungAnh::getNgayTao).reversed());
                NguoiDungAnh newMainAvatar = remainingAvatars.get(0);
                newMainAvatar.setLaAnhChinh(true);
                nguoiDungAnhRepository.save(newMainAvatar);
            }
        }
    }

    private String extractPublicIdFromUrl(String imageUrl) throws IOException {
        try {
            // ví dụ: http://res.cloudinary.com/cloud/image/upload/v123/folder/image.jpg
            // publicId cần lấy là "folder/image"
            
            int uploadIndex = imageUrl.indexOf("/upload/");
            if (uploadIndex == -1) {
                throw new IllegalArgumentException("URL không hợp lệ, không chứa '/upload/'");
            }

            // Lấy phần path sau /upload/ (có thể chứa version, ví dụ v123456/)
            String pathWithVersion = imageUrl.substring(uploadIndex + 8);

            String publicIdWithExtension;
            int firstSlashIndex = pathWithVersion.indexOf('/');
            
            // Bỏ qua version nếu có
            if (firstSlashIndex != -1 && pathWithVersion.startsWith("v")) {
                 publicIdWithExtension = pathWithVersion.substring(firstSlashIndex + 1);
            } else {
                 publicIdWithExtension = pathWithVersion;
            }
            
            int dotIndex = publicIdWithExtension.lastIndexOf('.');
            if (dotIndex == -1) {
                return publicIdWithExtension; // không có extension
            }

            return publicIdWithExtension.substring(0, dotIndex);
        } catch (Exception e) {
            throw new IOException("Không thể trích xuất public ID từ URL: " + imageUrl, e);
        }
    }

    @Override
    @Transactional
    public NguoiDungDTO thayDoiMucRiengTu(String token, com.mangxahoi.mangxahoi_backend.enums.CheDoBaiViet cheDoMoi) {
        NguoiDung nguoiDung = tokenUtil.layNguoiDungTuToken(token);
        nguoiDung.setMucRiengTu(cheDoMoi);
        NguoiDung updatedNguoiDung = nguoiDungRepository.save(nguoiDung);
        return chuyenSangDTO(updatedNguoiDung);
    }

    @Override
    public NguoiDungDTO capNhatCaiDatRiengTu(String token, PrivacySettingsRequest request) {
        NguoiDung nguoiDung = tokenUtil.layNguoiDungTuToken(token);

        if (request.getEmailCongKhai() != null) {
            nguoiDung.setEmailCongKhai(request.getEmailCongKhai());
        }
        if (request.getSdtCongKhai() != null) {
            nguoiDung.setSdtCongKhai(request.getSdtCongKhai());
        }
        if (request.getNgaySinhCongKhai() != null) {
            nguoiDung.setNgaySinhCongKhai(request.getNgaySinhCongKhai());
        }
        if (request.getGioiTinhCongKhai() != null) {
            nguoiDung.setGioiTinhCongKhai(request.getGioiTinhCongKhai());
        }

        NguoiDung nguoiDungDaLuu = nguoiDungRepository.save(nguoiDung);
        return chuyenSangDTO(nguoiDungDaLuu);
    }
}
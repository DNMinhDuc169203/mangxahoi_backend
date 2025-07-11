package com.mangxahoi.mangxahoi_backend.service;

import com.mangxahoi.mangxahoi_backend.dto.request.DangNhapRequest;
import com.mangxahoi.mangxahoi_backend.dto.response.DangNhapResponse;
import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import com.mangxahoi.mangxahoi_backend.dto.request.PrivacySettingsRequest;
import com.mangxahoi.mangxahoi_backend.dto.response.LichSuGoiYDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface NguoiDungService {
    NguoiDungDTO dangKy(NguoiDungDTO nguoiDungDTO, String matKhau);
    
    DangNhapResponse dangNhap(DangNhapRequest request);
    
    NguoiDungDTO capNhatThongTin(Integer id, NguoiDungDTO nguoiDungDTO);
    
    void xoaNguoiDung(Integer id);
    
    Optional<NguoiDungDTO> timTheoId(Integer profileId, String requesterToken);
    
    Optional<NguoiDungDTO> timTheoEmail(String email);
    
    Optional<NguoiDungDTO> timTheoSoDienThoai(String soDienThoai);
    
    Page<NguoiDungDTO> timTatCa(Pageable pageable);
    
    Page<NguoiDungDTO> timTheoHoTen(String hoTen, Pageable pageable);
    
    boolean xacThucEmail(String email, String token);
    
    boolean datLaiMatKhau(String email, String matKhauMoi);
    
    /**
     * Lấy thông tin người dùng hiện tại từ token
     * 
     * @param token Token xác thực
     * @return Thông tin người dùng hiện tại
     */
    NguoiDungDTO layThongTinHienTai(String token);
    
    /**
     * Upload ảnh đại diện cho người dùng
     * 
     * @param id ID của người dùng
     * @param file File ảnh
     * @param laAnhChinh Có phải là ảnh đại diện chính không
     * @return Đường dẫn của ảnh đại diện
     * @throws IOException Nếu có lỗi khi upload
     */
    String uploadAnhDaiDien(Integer id, MultipartFile file, boolean laAnhChinh) throws IOException;

    /**
     * Đổi mật khẩu cho người dùng hiện tại
     * @param token Token xác thực
     * @param matKhauCu Mật khẩu cũ
     * @param matKhauMoi Mật khẩu mới
     * @return true nếu đổi thành công, throw exception nếu lỗi
     */
    boolean doiMatKhau(String token, String matKhauCu, String matKhauMoi);

    /**
     * Xóa ảnh đại diện của người dùng
     * @param nguoiDungId ID của người dùng
     * @param anhId ID của ảnh
     * @throws IOException Nếu có lỗi khi xóa file trên Cloudinary
     */
    void xoaAnhDaiDien(Integer nguoiDungId, Integer anhId) throws IOException;

    /**
     * Thay đổi mức riêng tư cho người dùng hiện tại
     * @param token Token xác thực
     * @param cheDoMoi Chế độ riêng tư mới
     * @return NguoiDungDTO đã được cập nhật
     */
    NguoiDungDTO thayDoiMucRiengTu(String token, com.mangxahoi.mangxahoi_backend.enums.CheDoBaiViet cheDoMoi);

    /**
     * Cập nhật cài đặt riêng tư cho người dùng hiện tại
     * @param token Token xác thực
     * @param request DTO chứa các cài đặt riêng tư mới
     * @return NguoiDungDTO đã được cập nhật
     */
    NguoiDungDTO capNhatCaiDatRiengTu(String token, PrivacySettingsRequest request);

    Page<NguoiDungDTO> timTheoSoDienThoaiGanDung(String soDienThoai, Pageable pageable);

    /**
     * Lấy danh sách gợi ý kết bạn cho người dùng hiện tại
     * @param token Token xác thực
     * @return Danh sách gợi ý kết bạn
     */
    List<LichSuGoiYDTO> layGoiYKetBan(String token);
} 
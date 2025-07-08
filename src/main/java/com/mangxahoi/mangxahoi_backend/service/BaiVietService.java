package com.mangxahoi.mangxahoi_backend.service;

import com.mangxahoi.mangxahoi_backend.dto.BaiVietDTO;
import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import com.mangxahoi.mangxahoi_backend.entity.LichSuXuLyBaiViet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BaiVietService {
    BaiVietDTO taoBaiViet(Integer idNguoiDung, BaiVietDTO baiVietDTO, List<MultipartFile> media);
    
    BaiVietDTO capNhatBaiViet(Integer id, BaiVietDTO baiVietDTO, Integer idNguoiDung);
    
    void xoaBaiViet(Integer id, Integer idNguoiDung);
    
    Optional<BaiVietDTO> timTheoId(Integer id, Integer idNguoiDungHienTai);
    
    Page<BaiVietDTO> timBaiVietCuaNguoiDung(Integer idNguoiDung, Integer idNguoiDungHienTai, Pageable pageable);
    
    Page<BaiVietDTO> timBaiVietCongKhai(Integer idNguoiDungHienTai, Pageable pageable);
    
    Page<BaiVietDTO> timBaiVietXuHuong(Integer idNguoiDungHienTai, Pageable pageable);
    
    Page<BaiVietDTO> timBaiVietTheoHashtag(String tenHashtag, Integer idNguoiDungHienTai, Pageable pageable);
    
    boolean thichBaiViet(Integer idBaiViet, Integer idNguoiDung);
    
    boolean boThichBaiViet(Integer idBaiViet, Integer idNguoiDung);
    
    /**
     * Lấy newsfeed tổng hợp cho người dùng (giống Facebook)
     */
    Page<BaiVietDTO> layNewsfeedTongHop(Integer idNguoiDung, Pageable pageable);
    
    /**
     * Lấy danh sách người dùng đã thích bài viết
     * 
     * @param idBaiViet ID của bài viết
     * @return Danh sách người dùng đã thích
     */
    List<NguoiDungDTO> layDanhSachNguoiThichBaiViet(Integer idBaiViet);
    
    // Ẩn bài viết với quyền admin
    void anBaiVietByAdmin(Integer idBaiViet, Integer adminId, String lyDo);
    // Hiện (khôi phục) bài viết với quyền admin
    void hienBaiVietByAdmin(Integer idBaiViet, Integer adminId);
    // Xóa bài viết với quyền admin
    void xoaBaiVietByAdmin(Integer idBaiViet, Integer adminId, String lyDo);
    
    // Tìm kiếm nâng cao cho admin
    Page<BaiVietDTO> timKiemBaiVietAdmin(String keyword, String hashtag, String trangThai, String loai, Boolean sensitive, Pageable pageable);
    
    // Lấy lịch sử xử lý bài viết
    List<LichSuXuLyBaiViet> lichSuXuLyBaiViet(Integer idBaiViet);
    
    // Thống kê bài viết cho admin
    Map<String, Object> thongKeBaiViet(String fromDate, String toDate);

    List<BaiVietDTO> findTop5MoiNhat();
} 
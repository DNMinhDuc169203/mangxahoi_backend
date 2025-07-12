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
    
    List<NguoiDungDTO> layDanhSachNguoiThichBaiViet(Integer idBaiViet);
    
    void anBaiVietByAdmin(Integer idBaiViet, Integer adminId, String lyDo);
 
    void hienBaiVietByAdmin(Integer idBaiViet, Integer adminId);

    void xoaBaiVietByAdmin(Integer idBaiViet, Integer adminId, String lyDo);

    Page<BaiVietDTO> timKiemBaiVietAdmin(String keyword, String hashtag, String trangThai, String loai, Boolean sensitive, Pageable pageable);
 
    List<LichSuXuLyBaiViet> lichSuXuLyBaiViet(Integer idBaiViet);
    
    Map<String, Object> thongKeBaiViet(String fromDate, String toDate);

    List<BaiVietDTO> findTop5MoiNhat();
} 
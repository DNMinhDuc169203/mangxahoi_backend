package com.mangxahoi.mangxahoi_backend.service;

import com.mangxahoi.mangxahoi_backend.dto.BaiVietDTO;
import com.mangxahoi.mangxahoi_backend.dto.NguoiDungDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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
} 
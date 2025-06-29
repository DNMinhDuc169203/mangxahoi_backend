package com.mangxahoi.mangxahoi_backend.service;

import com.mangxahoi.mangxahoi_backend.dto.BinhLuanDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BinhLuanService {
    /**
     * Thêm bình luận mới
     * 
     * @param idBaiViet ID của bài viết
     * @param idNguoiDung ID của người dùng
     * @param idBinhLuanCha ID của bình luận cha (null nếu là bình luận gốc)
     * @param binhLuanDTO Thông tin bình luận
     * @return Thông tin bình luận đã thêm
     */
    BinhLuanDTO themBinhLuan(Integer idBaiViet, Integer idNguoiDung, Integer idBinhLuanCha, BinhLuanDTO binhLuanDTO);
    
    /**
     * Cập nhật bình luận
     * 
     * @param id ID của bình luận
     * @param idNguoiDung ID của người dùng (để kiểm tra quyền)
     * @param binhLuanDTO Thông tin bình luận cần cập nhật
     * @return Thông tin bình luận đã cập nhật
     */
    BinhLuanDTO capNhatBinhLuan(Integer id, Integer idNguoiDung, BinhLuanDTO binhLuanDTO);
    
    /**
     * Xóa bình luận
     * 
     * @param id ID của bình luận
     * @param idNguoiDung ID của người dùng (để kiểm tra quyền)
     * @return true nếu xóa thành công, false nếu không
     */
    boolean xoaBinhLuan(Integer id, Integer idNguoiDung);
    
    /**
     * Lấy danh sách bình luận gốc theo bài viết
     * 
     * @param idBaiViet ID của bài viết
     * @param pageable Thông tin phân trang
     * @return Danh sách bình luận gốc
     */
    Page<BinhLuanDTO> layBinhLuanGocTheoBaiViet(Integer idBaiViet, Pageable pageable);
    
    /**
     * Lấy danh sách bình luận phản hồi của một bình luận
     * 
     * @param idBinhLuanCha ID của bình luận cha
     * @param pageable Thông tin phân trang
     * @return Danh sách bình luận phản hồi
     */
    Page<BinhLuanDTO> layBinhLuanPhanHoi(Integer idBinhLuanCha, Pageable pageable);
    
    /**
     * Thích bình luận
     * 
     * @param idBinhLuan ID của bình luận
     * @param idNguoiDung ID của người dùng
     * @return true nếu thích thành công, false nếu không
     */
    boolean thichBinhLuan(Integer idBinhLuan, Integer idNguoiDung);
    
    /**
     * Bỏ thích bình luận
     * 
     * @param idBinhLuan ID của bình luận
     * @param idNguoiDung ID của người dùng
     * @return true nếu bỏ thích thành công, false nếu không
     */
    boolean boThichBinhLuan(Integer idBinhLuan, Integer idNguoiDung);
} 
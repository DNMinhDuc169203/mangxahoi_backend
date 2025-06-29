package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.BinhLuan;
import com.mangxahoi.mangxahoi_backend.entity.LuotThichBinhLuan;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LuotThichBinhLuanRepository extends JpaRepository<LuotThichBinhLuan, Integer> {
    List<LuotThichBinhLuan> findByBinhLuan(BinhLuan binhLuan);
    
    Page<LuotThichBinhLuan> findByBinhLuan(BinhLuan binhLuan, Pageable pageable);
    
    List<LuotThichBinhLuan> findByNguoiDung(NguoiDung nguoiDung);
    
    /**
     * Tìm lượt thích theo người dùng và bình luận
     * 
     * @param nguoiDung Người dùng
     * @param binhLuan Bình luận
     * @return Optional chứa lượt thích nếu tồn tại
     */
    Optional<LuotThichBinhLuan> findByNguoiDungAndBinhLuan(NguoiDung nguoiDung, BinhLuan binhLuan);
    
    /**
     * Tìm lượt thích theo ID người dùng và ID bình luận, chỉ lấy những lượt thích đang active
     * 
     * @param idNguoiDung ID người dùng
     * @param idBinhLuan ID bình luận
     * @return Optional chứa lượt thích nếu tồn tại và đang active
     */
    @Query("SELECT l FROM LuotThichBinhLuan l WHERE l.nguoiDung.id = :idNguoiDung AND l.binhLuan.id = :idBinhLuan AND l.trangThaiThich = true")
    Optional<LuotThichBinhLuan> findByNguoiDungIdAndBinhLuanIdAndTrangThaiThichTrue(
            @Param("idNguoiDung") Integer idNguoiDung, 
            @Param("idBinhLuan") Integer idBinhLuan);
    
    /**
     * Đếm số lượt thích của bình luận
     * 
     * @param binhLuan Bình luận
     * @return Số lượt thích
     */
    long countByBinhLuanAndTrangThaiThichTrue(BinhLuan binhLuan);
    
    /**
     * Lấy danh sách người dùng đã thích bình luận
     * 
     * @param binhLuan Bình luận
     * @return Danh sách người dùng
     */
    @Query("SELECT l.nguoiDung FROM LuotThichBinhLuan l WHERE l.binhLuan = :binhLuan AND l.trangThaiThich = true")
    List<NguoiDung> findNguoiDungsByBinhLuanAndTrangThaiThichTrue(@Param("binhLuan") BinhLuan binhLuan);
    
    boolean existsByNguoiDungAndBinhLuanAndTrangThaiThich(NguoiDung nguoiDung, BinhLuan binhLuan, Boolean trangThaiThich);
    
    /**
     * Xóa tất cả lượt thích của bình luận
     * 
     * @param binhLuan Bình luận
     */
    void deleteByBinhLuan(BinhLuan binhLuan);
    
    void deleteByNguoiDung(NguoiDung nguoiDung);
} 
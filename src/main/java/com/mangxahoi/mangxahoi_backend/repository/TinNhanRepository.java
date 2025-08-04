package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.CuocTroChuyen;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.TinNhan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TinNhanRepository extends JpaRepository<TinNhan, Integer> {
    @Query("SELECT t FROM TinNhan t WHERE t.cuocTroChuyen = :cuocTroChuyen AND (t.daXoa IS NULL OR t.daXoa = false)")
    List<TinNhan> findByCuocTroChuyen(@Param("cuocTroChuyen") CuocTroChuyen cuocTroChuyen);
    
    @Query("SELECT t FROM TinNhan t WHERE t.cuocTroChuyen = :cuocTroChuyen AND (t.daXoa IS NULL OR t.daXoa = false) ORDER BY t.ngayTao DESC")
    Page<TinNhan> findByCuocTroChuyenOrderByNgayTaoDesc(@Param("cuocTroChuyen") CuocTroChuyen cuocTroChuyen, Pageable pageable);
    
    List<TinNhan> findByNguoiGui(NguoiDung nguoiGui);
    
    @Query("SELECT t FROM TinNhan t WHERE t.cuocTroChuyen = :cuocTroChuyen AND t.ngayTao > :lastSeen AND (t.daXoa IS NULL OR t.daXoa = false) ORDER BY t.ngayTao ASC")
    List<TinNhan> findUnreadMessages(
            @Param("cuocTroChuyen") CuocTroChuyen cuocTroChuyen,
            @Param("lastSeen") LocalDateTime lastSeen);
    
    @Query("SELECT COUNT(t) FROM TinNhan t WHERE t.cuocTroChuyen = :cuocTroChuyen AND t.daDoc = false AND t.nguoiGui != :currentUser AND (t.daXoa IS NULL OR t.daXoa = false)")
    long countUnreadMessagesByConversation(
            @Param("cuocTroChuyen") CuocTroChuyen cuocTroChuyen,
            @Param("currentUser") NguoiDung currentUser);
    
    @Query("SELECT t FROM TinNhan t WHERE t.cuocTroChuyen = :cuocTroChuyen AND t.noiDung LIKE %:keyword% AND (t.daXoa IS NULL OR t.daXoa = false)")
    Page<TinNhan> searchMessagesInConversation(
            @Param("cuocTroChuyen") CuocTroChuyen cuocTroChuyen,
            @Param("keyword") String keyword,
            Pageable pageable);
    
    void deleteByCuocTroChuyen(CuocTroChuyen cuocTroChuyen);
    
    void deleteByNguoiGui(NguoiDung nguoiGui);
    void deleteAllByCuocTroChuyen(CuocTroChuyen cuocTroChuyen);

    @Query("SELECT t FROM TinNhan t WHERE t.cuocTroChuyen.id = :idCuocTroChuyen AND t.nguoiGui.id != :idNguoiDoc AND t.daDoc = false AND (t.daXoa IS NULL OR t.daXoa = false)")
    List<TinNhan> findByCuocTroChuyenIdAndNguoiGuiIdNotAndDaDocFalse(@Param("idCuocTroChuyen") Integer idCuocTroChuyen, @Param("idNguoiDoc") Integer idNguoiDoc);
    
    @Query("SELECT t FROM TinNhan t WHERE t.cuocTroChuyen = :cuocTroChuyen AND t.nguoiGui = :nguoiGui AND (t.daXoa IS NULL OR t.daXoa = false)")
    List<TinNhan> findByCuocTroChuyenAndNguoiGui(@Param("cuocTroChuyen") CuocTroChuyen cuocTroChuyen, @Param("nguoiGui") NguoiDung nguoiGui);
}
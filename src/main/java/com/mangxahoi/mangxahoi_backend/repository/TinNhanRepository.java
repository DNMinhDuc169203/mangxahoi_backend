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
    List<TinNhan> findByCuocTroChuyen(CuocTroChuyen cuocTroChuyen);
    
    Page<TinNhan> findByCuocTroChuyenOrderByNgayTaoDesc(CuocTroChuyen cuocTroChuyen, Pageable pageable);
    
    List<TinNhan> findByNguoiGui(NguoiDung nguoiGui);
    
    @Query("SELECT t FROM TinNhan t WHERE t.cuocTroChuyen = :cuocTroChuyen AND t.ngayTao > :lastSeen ORDER BY t.ngayTao ASC")
    List<TinNhan> findUnreadMessages(
            @Param("cuocTroChuyen") CuocTroChuyen cuocTroChuyen,
            @Param("lastSeen") LocalDateTime lastSeen);
    
    @Query("SELECT COUNT(t) FROM TinNhan t WHERE t.cuocTroChuyen = :cuocTroChuyen AND t.daDoc = false AND t.nguoiGui != :currentUser")
    long countUnreadMessagesByConversation(
            @Param("cuocTroChuyen") CuocTroChuyen cuocTroChuyen,
            @Param("currentUser") NguoiDung currentUser);
    
    @Query("SELECT t FROM TinNhan t WHERE t.cuocTroChuyen = :cuocTroChuyen AND t.noiDung LIKE %:keyword%")
    Page<TinNhan> searchMessagesInConversation(
            @Param("cuocTroChuyen") CuocTroChuyen cuocTroChuyen,
            @Param("keyword") String keyword,
            Pageable pageable);
    
    void deleteByCuocTroChuyen(CuocTroChuyen cuocTroChuyen);
    
    void deleteByNguoiGui(NguoiDung nguoiGui);

    List<TinNhan> findByCuocTroChuyenIdAndNguoiGuiIdNotAndDaDocFalse(Integer idCuocTroChuyen, Integer idNguoiDoc);
}
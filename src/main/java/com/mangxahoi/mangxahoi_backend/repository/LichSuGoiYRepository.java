package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.DiemTuongTac;
import com.mangxahoi.mangxahoi_backend.entity.LichSuGoiY;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.enums.NguonGoiY;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LichSuGoiYRepository extends JpaRepository<LichSuGoiY, Integer> {
    List<LichSuGoiY> findByNguoiDuocGoiY(NguoiDung nguoiDuocGoiY);
    
    List<LichSuGoiY> findByNguoiTrongGoiY(NguoiDung nguoiTrongGoiY);
    
    Optional<LichSuGoiY> findByNguoiDuocGoiYAndNguoiTrongGoiY(NguoiDung nguoiDuocGoiY, NguoiDung nguoiTrongGoiY);
    
    List<LichSuGoiY> findByDiemTuongTac(DiemTuongTac diemTuongTac);
    
    @Query("SELECT l FROM LichSuGoiY l WHERE l.nguoiDuocGoiY = :nguoiDung AND l.daXem = false ORDER BY l.diemGoiY DESC")
    Page<LichSuGoiY> findUnseenSuggestions(@Param("nguoiDung") NguoiDung nguoiDung, Pageable pageable);
    
    @Query("SELECT l FROM LichSuGoiY l WHERE l.nguoiDuocGoiY = :nguoiDung AND l.nguonGoiY = :nguonGoiY ORDER BY l.diemGoiY DESC")
    Page<LichSuGoiY> findByNguoiDuocGoiYAndNguonGoiY(
            @Param("nguoiDung") NguoiDung nguoiDung, 
            @Param("nguonGoiY") NguonGoiY nguonGoiY, 
            Pageable pageable);
    
    @Query("SELECT COUNT(l) FROM LichSuGoiY l WHERE l.nguoiDuocGoiY = :nguoiDung AND l.daXem = false")
    long countUnseenSuggestions(@Param("nguoiDung") NguoiDung nguoiDung);
    
    @Query("SELECT l FROM LichSuGoiY l WHERE l.nguoiDuocGoiY = :nguoiDung AND l.ngayGoiY >= :startDate ORDER BY l.ngayGoiY DESC")
    List<LichSuGoiY> findRecentSuggestions(
            @Param("nguoiDung") NguoiDung nguoiDung, 
            @Param("startDate") LocalDateTime startDate);
    
    void deleteByNguoiDuocGoiY(NguoiDung nguoiDuocGoiY);
    
    void deleteByNguoiTrongGoiY(NguoiDung nguoiTrongGoiY);
    
    void deleteByDiemTuongTac(DiemTuongTac diemTuongTac);
} 
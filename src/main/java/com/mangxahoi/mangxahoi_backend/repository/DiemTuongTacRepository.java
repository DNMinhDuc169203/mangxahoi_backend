package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.DiemTuongTac;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.enums.MucDoThanThiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiemTuongTacRepository extends JpaRepository<DiemTuongTac, Integer> {
    List<DiemTuongTac> findByNguoi1(NguoiDung nguoi1);
    
    List<DiemTuongTac> findByNguoi2(NguoiDung nguoi2);
    
    Optional<DiemTuongTac> findByNguoi1AndNguoi2(NguoiDung nguoi1, NguoiDung nguoi2);
    
    @Query("SELECT d FROM DiemTuongTac d WHERE d.nguoi1 = :nguoiDung ORDER BY d.tongDiem DESC")
    Page<DiemTuongTac> findTopInteractionsByUser(@Param("nguoiDung") NguoiDung nguoiDung, Pageable pageable);
    
    @Query("SELECT d FROM DiemTuongTac d WHERE d.nguoi1 = :nguoiDung AND d.mucDoThanThiet = :mucDoThanThiet ORDER BY d.tongDiem DESC")
    List<DiemTuongTac> findByNguoi1AndMucDoThanThiet(
            @Param("nguoiDung") NguoiDung nguoiDung, 
            @Param("mucDoThanThiet") MucDoThanThiet mucDoThanThiet);
    
    @Query("SELECT d FROM DiemTuongTac d WHERE d.nguoi1 = :nguoiDung AND d.trangThaiGoiY = 'chua_goi_y' ORDER BY d.tongDiem DESC")
    Page<DiemTuongTac> findPotentialSuggestions(@Param("nguoiDung") NguoiDung nguoiDung, Pageable pageable);
    
    void deleteByNguoi1(NguoiDung nguoi1);
    
    void deleteByNguoi2(NguoiDung nguoi2);
} 
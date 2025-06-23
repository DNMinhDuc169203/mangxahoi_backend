package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDungAnh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NguoiDungAnhRepository extends JpaRepository<NguoiDungAnh, Integer> {
    List<NguoiDungAnh> findByNguoiDung(NguoiDung nguoiDung);
    
    Optional<NguoiDungAnh> findByNguoiDungAndLaAnhChinh(NguoiDung nguoiDung, Boolean laAnhChinh);
} 
package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.TinNhanDaDoc;
import com.mangxahoi.mangxahoi_backend.entity.TinNhan;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TinNhanDaDocRepository extends JpaRepository<TinNhanDaDoc, Integer> {
    boolean existsByTinNhanAndNguoiDoc(TinNhan tinNhan, NguoiDung nguoiDoc);
    List<TinNhanDaDoc> findByTinNhan(TinNhan tinNhan);
    List<TinNhanDaDoc> findByTinNhanId(Integer tinNhanId);
} 
package com.mangxahoi.mangxahoi_backend.repository;

import com.mangxahoi.mangxahoi_backend.entity.TinNhanDaDoc;
import com.mangxahoi.mangxahoi_backend.entity.TinNhan;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.CuocTroChuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TinNhanDaDocRepository extends JpaRepository<TinNhanDaDoc, Integer> {
    boolean existsByTinNhanAndNguoiDoc(TinNhan tinNhan, NguoiDung nguoiDoc);
    List<TinNhanDaDoc> findByTinNhan(TinNhan tinNhan);
    List<TinNhanDaDoc> findByTinNhanId(Integer tinNhanId);

    @Query("SELECT COUNT(t) FROM TinNhan t WHERE t.cuocTroChuyen = :cuocTroChuyen AND t.id NOT IN (SELECT d.tinNhan.id FROM TinNhanDaDoc d WHERE d.nguoiDoc = :nguoiDoc)")
    long countUnreadGroupMessagesByConversation(@Param("cuocTroChuyen") CuocTroChuyen cuocTroChuyen, @Param("nguoiDoc") NguoiDung nguoiDoc);
} 
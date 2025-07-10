package com.mangxahoi.mangxahoi_backend.service;

import com.mangxahoi.mangxahoi_backend.entity.TinNhanDaDoc;
import com.mangxahoi.mangxahoi_backend.entity.TinNhan;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import java.util.List;

public interface TinNhanDaDocService {
    void danhDauDaDoc(TinNhan tinNhan, NguoiDung nguoiDoc);
    List<TinNhanDaDoc> layDanhSachNguoiDoc(TinNhan tinNhan);
    List<TinNhanDaDoc> layDanhSachNguoiDocTheoIdTinNhan(Integer tinNhanId);

    // Kiểm tra đã đọc tin nhắn nhóm chưa
    boolean daDoc(TinNhan tinNhan, NguoiDung nguoiDoc);
} 
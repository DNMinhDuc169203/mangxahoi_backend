package com.mangxahoi.mangxahoi_backend.service.impl;

import com.mangxahoi.mangxahoi_backend.entity.TinNhanDaDoc;
import com.mangxahoi.mangxahoi_backend.entity.TinNhan;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.repository.TinNhanDaDocRepository;
import com.mangxahoi.mangxahoi_backend.service.TinNhanDaDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TinNhanDaDocServiceImpl implements TinNhanDaDocService {
    @Autowired
    private TinNhanDaDocRepository tinNhanDaDocRepository;

    @Override
    public void danhDauDaDoc(TinNhan tinNhan, NguoiDung nguoiDoc) {
        if (!tinNhanDaDocRepository.existsByTinNhanAndNguoiDoc(tinNhan, nguoiDoc)) {
            TinNhanDaDoc daDoc = new TinNhanDaDoc();
            daDoc.setTinNhan(tinNhan);
            daDoc.setNguoiDoc(nguoiDoc);
            daDoc.setThoiGianDoc(LocalDateTime.now());
            tinNhanDaDocRepository.save(daDoc);
        }
    }

    @Override
    public List<TinNhanDaDoc> layDanhSachNguoiDoc(TinNhan tinNhan) {
        return tinNhanDaDocRepository.findByTinNhan(tinNhan);
    }

    @Override
    public List<TinNhanDaDoc> layDanhSachNguoiDocTheoIdTinNhan(Integer tinNhanId) {
        return tinNhanDaDocRepository.findByTinNhanId(tinNhanId);
    }
} 
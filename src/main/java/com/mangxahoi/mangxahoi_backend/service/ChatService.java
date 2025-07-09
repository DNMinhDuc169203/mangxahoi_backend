 package com.mangxahoi.mangxahoi_backend.service;


import com.mangxahoi.mangxahoi_backend.dto.request.*;
import com.mangxahoi.mangxahoi_backend.dto.response.*;
import com.mangxahoi.mangxahoi_backend.entity.*;
import java.util.List;

public interface ChatService {
    GuiTinNhanResponse guiTinNhan(GuiTinNhanRequest request);
    TaoCuocTroChuyenResponse taoCuocTroChuyen(TaoCuocTroChuyenRequest request);
    void themThanhVien(ThemThanhVienRequest request);
    void thuHoiTinNhan(ThuHoiTinNhanRequest request);
    List<GuiTinNhanResponse> timKiemTinNhan(TimKiemTinNhanRequest request);
    List<TaoCuocTroChuyenResponse> layDanhSachCuocTroChuyen(Integer idNguoiDung);

    void markMessagesAsRead(Integer idCuocTroChuyen, Integer idNguoiDoc);
}

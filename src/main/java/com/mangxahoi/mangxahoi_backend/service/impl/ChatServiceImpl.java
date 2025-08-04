package com.mangxahoi.mangxahoi_backend.service.impl;

import com.mangxahoi.mangxahoi_backend.dto.*;
import com.mangxahoi.mangxahoi_backend.dto.request.*;
import com.mangxahoi.mangxahoi_backend.dto.response.*;
import com.mangxahoi.mangxahoi_backend.entity.TinNhan;
import com.mangxahoi.mangxahoi_backend.entity.CuocTroChuyen;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.ThanhVienCuocTroChuyen;
import com.mangxahoi.mangxahoi_backend.enums.LoaiCuocTroChuyen;
import com.mangxahoi.mangxahoi_backend.enums.LoaiTinNhan;
import com.mangxahoi.mangxahoi_backend.enums.VaiTroThanhVien;
import com.mangxahoi.mangxahoi_backend.enums.TrangThaiKetBan;
import com.mangxahoi.mangxahoi_backend.repository.TinNhanRepository;
import com.mangxahoi.mangxahoi_backend.repository.CuocTroChuyenRepository;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import com.mangxahoi.mangxahoi_backend.repository.ThanhVienCuocTroChuyenRepository;
import com.mangxahoi.mangxahoi_backend.repository.TinNhanDaDocRepository;
import com.mangxahoi.mangxahoi_backend.entity.TinNhanDaDoc;
import com.mangxahoi.mangxahoi_backend.service.ChatService;
import com.mangxahoi.mangxahoi_backend.service.ThongBaoService;
import com.mangxahoi.mangxahoi_backend.service.KetBanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private TinNhanRepository tinNhanRepository;
    @Autowired
    private CuocTroChuyenRepository cuocTroChuyenRepository;
    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    @Autowired
    private ThanhVienCuocTroChuyenRepository thanhVienCuocTroChuyenRepository;
    @Autowired
    private TinNhanDaDocRepository tinNhanDaDocRepository;
    @Autowired
    private ThongBaoService thongBaoService;
    @Autowired
    private KetBanService ketBanService;

    @Override
    public GuiTinNhanResponse guiTinNhan(GuiTinNhanRequest request) {
        CuocTroChuyen cuocTroChuyen = cuocTroChuyenRepository.findById(request.getIdCuocTroChuyen())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc trò chuyện"));
        NguoiDung nguoiGui = nguoiDungRepository.findById(request.getIdNguoiGui())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người gửi"));
        // Bổ sung kiểm tra trạng thái bạn bè nếu là chat cá nhân
        if (cuocTroChuyen.getLoai() == LoaiCuocTroChuyen.ca_nhan) {
            List<ThanhVienCuocTroChuyen> members = thanhVienCuocTroChuyenRepository.findByCuocTroChuyen(cuocTroChuyen);
            if (members.size() == 2) {
                Integer id1 = members.get(0).getNguoiDung().getId();
                Integer id2 = members.get(1).getNguoiDung().getId();
                TrangThaiKetBan trangThai = ketBanService.kiemTraTrangThaiKetBan(id1, id2);
                if (trangThai != TrangThaiKetBan.ban_be) {
                    throw new RuntimeException("Hai người không còn là bạn bè, không thể gửi tin nhắn.");
                }
            }
        }

        TinNhan tinNhan = new TinNhan();
        tinNhan.setCuocTroChuyen(cuocTroChuyen);
        tinNhan.setNguoiGui(nguoiGui);
        tinNhan.setNoiDung(request.getNoiDung());
        tinNhan.setLoaiTinNhan(LoaiTinNhan.valueOf(request.getLoaiTinNhan()));
        tinNhan.setUrlTepTin(request.getUrlTepTin());
        tinNhan.setDaDoc(false);
        tinNhan.setNgayTao(LocalDateTime.now());

        tinNhan = tinNhanRepository.save(tinNhan);

        cuocTroChuyen.setTinNhanCuoi(tinNhan.getNgayTao());
        cuocTroChuyenRepository.save(cuocTroChuyen);

        // Gửi thông báo tin nhắn mới
        if (cuocTroChuyen.getLoai().name().equals("ca_nhan")) {
            thongBaoService.guiThongBaoTinNhan(nguoiGui.getId(), tinNhan.getId(), cuocTroChuyen.getId());
        } else if (cuocTroChuyen.getLoai().name().equals("nhom")) {
            // Gửi thông báo cho tất cả thành viên nhóm (trừ người gửi)
            List<ThanhVienCuocTroChuyen> members = thanhVienCuocTroChuyenRepository.findByCuocTroChuyen(cuocTroChuyen);
            for (ThanhVienCuocTroChuyen tv : members) {
                if (!tv.getNguoiDung().getId().equals(nguoiGui.getId())) {
                    thongBaoService.guiThongBaoTinNhan(tv.getNguoiDung().getId(), tinNhan.getId(), cuocTroChuyen.getId());
                }
            }
        }

        GuiTinNhanResponse response = new GuiTinNhanResponse();
        response.setIdTinNhan(tinNhan.getId());
        response.setIdCuocTroChuyen(cuocTroChuyen.getId());
        response.setIdNguoiGui(nguoiGui.getId());
        response.setNoiDung(tinNhan.getNoiDung());
        response.setLoaiTinNhan(tinNhan.getLoaiTinNhan().name());
        response.setUrlTepTin(tinNhan.getUrlTepTin());
        response.setDaDoc(tinNhan.getDaDoc());
        response.setNgayTao(tinNhan.getNgayTao());
        return response;
    }

    @Override
    public TaoCuocTroChuyenResponse taoCuocTroChuyen(TaoCuocTroChuyenRequest request) {
        List<Integer> idThanhVien = request.getIdThanhVien();
        System.out.println("Backend nhận idThanhVien: " + idThanhVien + ", size: " + idThanhVien.size());
        if (idThanhVien == null || idThanhVien.size() < 2) {
            throw new RuntimeException("Cần ít nhất 2 thành viên để tạo cuộc trò chuyện");
        }
        List<NguoiDung> thanhVienEntities = new ArrayList<>();
        for (Integer id : idThanhVien) {
            NguoiDung nd = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id: " + id));
            thanhVienEntities.add(nd);
        }
        LoaiCuocTroChuyen loai = idThanhVien.size() == 2 ? LoaiCuocTroChuyen.ca_nhan : LoaiCuocTroChuyen.nhom;
        // Nếu là cá nhân, kiểm tra đã có cuộc trò chuyện chưa
        if (loai == LoaiCuocTroChuyen.ca_nhan) {
            NguoiDung nguoi1 = thanhVienEntities.get(0);
            NguoiDung nguoi2 = thanhVienEntities.get(1);
            NguoiDung doiPhuong = nguoi1.getId().equals(request.getIdNguoiTao()) ? nguoi2 : nguoi1;
            
            // Tìm cuộc trò chuyện cá nhân hiện có bằng cách tìm qua ThanhVienCuocTroChuyen
            List<ThanhVienCuocTroChuyen> thanhVien1 = thanhVienCuocTroChuyenRepository.findByNguoiDung(nguoi1);
            List<ThanhVienCuocTroChuyen> thanhVien2 = thanhVienCuocTroChuyenRepository.findByNguoiDung(nguoi2);
            
            // Tìm cuộc trò chuyện chung giữa 2 người
            CuocTroChuyen existingConv = null;
            for (ThanhVienCuocTroChuyen tv1 : thanhVien1) {
                for (ThanhVienCuocTroChuyen tv2 : thanhVien2) {
                    if (tv1.getCuocTroChuyen().getId().equals(tv2.getCuocTroChuyen().getId()) 
                        && tv1.getCuocTroChuyen().getLoai() == LoaiCuocTroChuyen.ca_nhan) {
                        existingConv = tv1.getCuocTroChuyen();
                        break;
                    }
                }
                if (existingConv != null) break;
            }
            
            if (existingConv != null) {
                return TaoCuocTroChuyenResponse.builder()
                    .idCuocTroChuyen(existingConv.getId())
                    .loai(existingConv.getLoai().name())
                    .tenNhom(existingConv.getTenNhom())
                    .anhNhom(existingConv.getAnhNhom())
                    .idNguoiTao(existingConv.getNguoiTao() != null ? existingConv.getNguoiTao().getId() : null)
                    .idThanhVien(new ArrayList<>(idThanhVien))
                    .idDoiPhuong(doiPhuong.getId())
                    .tenDoiPhuong(doiPhuong.getHoTen())
                    .anhDaiDienDoiPhuong(
                        (doiPhuong.getAnhDaiDien() != null && !doiPhuong.getAnhDaiDien().isEmpty())
                            ? doiPhuong.getAnhDaiDien().get(0).getUrl()
                            : null
                    )
                    .build();
            }
        }
        // Tạo mới cuộc trò chuyện
        CuocTroChuyen cuoc = new CuocTroChuyen();
        cuoc.setLoai(loai);
        if (loai == LoaiCuocTroChuyen.nhom) {
            cuoc.setTenNhom(request.getTenNhom());
            cuoc.setAnhNhom(request.getAnhNhom());
        }
        cuoc.setNguoiTao(nguoiDungRepository.findById(request.getIdNguoiTao())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người tạo")));
        cuoc.setSoThanhVien(idThanhVien.size());
        cuoc = cuocTroChuyenRepository.save(cuoc);
        // Thêm thành viên
        for (NguoiDung nd : thanhVienEntities) {
            ThanhVienCuocTroChuyen tv = new ThanhVienCuocTroChuyen();
            tv.setCuocTroChuyen(cuoc);
            tv.setNguoiDung(nd);
            if (nd.getId().equals(request.getIdNguoiTao())) {
                tv.setVaiTro(VaiTroThanhVien.quan_tri);
            } else {
                tv.setVaiTro(VaiTroThanhVien.thanh_vien);
            }
            thanhVienCuocTroChuyenRepository.save(tv);
        }
        if (loai == LoaiCuocTroChuyen.ca_nhan) {
            NguoiDung nguoi1 = thanhVienEntities.get(0);
            NguoiDung nguoi2 = thanhVienEntities.get(1);
            NguoiDung doiPhuong = nguoi1.getId().equals(request.getIdNguoiTao()) ? nguoi2 : nguoi1;
            return TaoCuocTroChuyenResponse.builder()
                .idCuocTroChuyen(cuoc.getId())
                .loai(cuoc.getLoai().name())
                .tenNhom(cuoc.getTenNhom())
                .anhNhom(cuoc.getAnhNhom())
                .idNguoiTao(cuoc.getNguoiTao().getId())
                .idThanhVien(new ArrayList<>(idThanhVien))
                .idDoiPhuong(doiPhuong.getId())
                .tenDoiPhuong(doiPhuong.getHoTen())
                .anhDaiDienDoiPhuong(
                    (doiPhuong.getAnhDaiDien() != null && !doiPhuong.getAnhDaiDien().isEmpty())
                        ? doiPhuong.getAnhDaiDien().get(0).getUrl()
                        : null
                )
                .build();
        }
        return TaoCuocTroChuyenResponse.builder()
            .idCuocTroChuyen(cuoc.getId())
            .loai(cuoc.getLoai().name())
            .tenNhom(cuoc.getTenNhom())
            .anhNhom(cuoc.getAnhNhom())
            .idNguoiTao(cuoc.getNguoiTao().getId())
            .idThanhVien(new ArrayList<>(idThanhVien))
            .build();
    }

    @Override
    @Transactional
    public void themThanhVien(ThemThanhVienRequest request) {
        CuocTroChuyen cuocTroChuyen = cuocTroChuyenRepository.findById(request.getIdCuocTroChuyen())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc trò chuyện"));
        
        // Kiểm tra cuộc trò chuyện phải là nhóm
        if (cuocTroChuyen.getLoai() != LoaiCuocTroChuyen.nhom) {
            throw new RuntimeException("Chỉ có thể thêm thành viên vào cuộc trò chuyện nhóm");
        }
        
        // Kiểm tra người thực hiện có quyền không (phải là quản trị viên hoặc người tạo nhóm)
        ThanhVienCuocTroChuyen nguoiThucHien = thanhVienCuocTroChuyenRepository
            .findByCuocTroChuyenAndNguoiDung(cuocTroChuyen, 
                nguoiDungRepository.findById(request.getIdNguoiThucHien())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người thực hiện")))
            .orElseThrow(() -> new RuntimeException("Người thực hiện không phải thành viên của nhóm"));
        boolean isAdmin = nguoiThucHien.getVaiTro() == VaiTroThanhVien.quan_tri;
        boolean isCreator = cuocTroChuyen.getNguoiTao() != null && cuocTroChuyen.getNguoiTao().getId().equals(request.getIdNguoiThucHien());
        if (!isAdmin && !isCreator) {
            throw new RuntimeException("Chỉ quản trị viên hoặc người tạo nhóm mới có quyền thêm thành viên");
        }
        
        // Thêm từng thành viên mới
        List<Integer> idThanhVienMoiList = request.getIdThanhVienMoi();
        if (idThanhVienMoiList == null || idThanhVienMoiList.isEmpty()) {
            throw new RuntimeException("Danh sách thành viên mới không được để trống");
        }
        List<String> tenThanhVienMoi = new ArrayList<>();
        for (Integer idThanhVienMoi : idThanhVienMoiList) {
            NguoiDung thanhVienMoi = nguoiDungRepository.findById(idThanhVienMoi)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id: " + idThanhVienMoi));
            
            // Kiểm tra thành viên đã có trong nhóm chưa
            if (thanhVienCuocTroChuyenRepository.existsByCuocTroChuyenAndNguoiDung(cuocTroChuyen, thanhVienMoi)) {
                throw new RuntimeException("Thành viên với id " + idThanhVienMoi + " đã có trong nhóm");
            }
            
            // Thêm thành viên mới
            ThanhVienCuocTroChuyen thanhVienMoiEntity = new ThanhVienCuocTroChuyen();
            thanhVienMoiEntity.setCuocTroChuyen(cuocTroChuyen);
            thanhVienMoiEntity.setNguoiDung(thanhVienMoi);
            thanhVienMoiEntity.setVaiTro(VaiTroThanhVien.thanh_vien);
            thanhVienCuocTroChuyenRepository.save(thanhVienMoiEntity);
            tenThanhVienMoi.add(thanhVienMoi.getHoTen());
        }
        
        // Cập nhật số thành viên
        long soThanhVienHienTai = thanhVienCuocTroChuyenRepository.countByCuocTroChuyen(cuocTroChuyen);
        cuocTroChuyen.setSoThanhVien((int) soThanhVienHienTai);
        cuocTroChuyenRepository.save(cuocTroChuyen);

        // Tạo tin nhắn thông báo khi thêm thành viên
        if (!tenThanhVienMoi.isEmpty()) {
            TinNhan thongBao = new TinNhan();
            thongBao.setCuocTroChuyen(cuocTroChuyen);
            thongBao.setNguoiGui(nguoiThucHien.getNguoiDung());
            String tenStr = String.join(", ", tenThanhVienMoi);
            thongBao.setNoiDung(tenStr + (tenThanhVienMoi.size() > 1 ? " đã được thêm vào nhóm" : " đã được thêm vào nhóm"));
            thongBao.setLoaiTinNhan(LoaiTinNhan.thong_bao);
            thongBao.setNgayTao(LocalDateTime.now());
            thongBao.setDaDoc(false);
            tinNhanRepository.save(thongBao);
        }
    }

    @Override
    public void thuHoiTinNhan(ThuHoiTinNhanRequest request) {
        TinNhan tinNhan = tinNhanRepository.findById(request.getIdTinNhan())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tin nhắn"));
        
        // Kiểm tra người thực hiện có phải là người gửi tin nhắn không
        if (!tinNhan.getNguoiGui().getId().equals(request.getIdNguoiThucHien())) {
            throw new RuntimeException("Chỉ người gửi tin nhắn mới có thể thu hồi");
        }
        
        // Kiểm tra tin nhắn đã bị thu hồi chưa
        if (tinNhan.getDaXoa() != null && tinNhan.getDaXoa()) {
            throw new RuntimeException("Tin nhắn đã được thu hồi trước đó");
        }
        
        // Đánh dấu tin nhắn đã xóa
        tinNhan.setDaXoa(true);
        tinNhanRepository.save(tinNhan);
    }

    @Override
    public void xoaTinNhan(Integer idTinNhan, Integer idCuocTroChuyen, Integer idNguoiThucHien) {
        TinNhan tinNhan = tinNhanRepository.findById(idTinNhan)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tin nhắn"));
        
        CuocTroChuyen cuocTroChuyen = cuocTroChuyenRepository.findById(idCuocTroChuyen)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc trò chuyện"));
        
        // Kiểm tra người thực hiện có phải là người gửi tin nhắn không
        if (!tinNhan.getNguoiGui().getId().equals(idNguoiThucHien)) {
            throw new RuntimeException("Chỉ người gửi tin nhắn mới có thể xóa");
        }
        
        // Kiểm tra tin nhắn thuộc về cuộc trò chuyện đúng không
        if (!tinNhan.getCuocTroChuyen().getId().equals(idCuocTroChuyen)) {
            throw new RuntimeException("Tin nhắn không thuộc về cuộc trò chuyện này");
        }
        
        // Kiểm tra tin nhắn đã bị xóa chưa
        if (tinNhan.getDaXoa() != null && tinNhan.getDaXoa()) {
            throw new RuntimeException("Tin nhắn đã được xóa trước đó");
        }
        
        // Xóa mềm tin nhắn
        tinNhan.setDaXoa(true);
        tinNhanRepository.save(tinNhan);
    }

    @Override
    @Transactional
    public void xoaToanBoTinNhan(Integer idCuocTroChuyen, Integer idNguoiThucHien) {
        CuocTroChuyen cuocTroChuyen = cuocTroChuyenRepository.findById(idCuocTroChuyen)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc trò chuyện"));
        
        // Kiểm tra người thực hiện có phải là thành viên của cuộc trò chuyện không
        ThanhVienCuocTroChuyen thanhVien = thanhVienCuocTroChuyenRepository.findByCuocTroChuyenAndNguoiDung(cuocTroChuyen, nguoiDungRepository.findById(idNguoiThucHien).orElse(null))
            .orElseThrow(() -> new RuntimeException("Bạn không phải thành viên của cuộc trò chuyện này"));
        
        // Lấy tất cả tin nhắn trong cuộc trò chuyện
        List<TinNhan> tinNhans = tinNhanRepository.findByCuocTroChuyen(cuocTroChuyen);
        
        // Xóa mềm tất cả tin nhắn trong cuộc trò chuyện
        for (TinNhan tinNhan : tinNhans) {
            if (tinNhan.getDaXoa() == null || !tinNhan.getDaXoa()) {
                tinNhan.setDaXoa(true);
                tinNhanRepository.save(tinNhan);
            }
        }
        
        // Xóa thành viên khỏi cuộc trò chuyện (xóa khung cuộc trò chuyện)
        thanhVienCuocTroChuyenRepository.delete(thanhVien);
    }

    @Override
    public List<GuiTinNhanResponse> timKiemTinNhan(TimKiemTinNhanRequest request) {
        CuocTroChuyen cuocTroChuyen = cuocTroChuyenRepository.findById(request.getIdCuocTroChuyen())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc trò chuyện"));
        boolean isGroup = cuocTroChuyen.getLoai().name().equals("nhom");
        // Tạo pageable cho phân trang
        Pageable pageable = PageRequest.of(
            request.getTrang() != null ? request.getTrang() : 0,
            request.getKichThuoc() != null ? request.getKichThuoc() : 10
        );
        
        // Tìm kiếm tin nhắn theo từ khóa
        Page<TinNhan> tinNhans = tinNhanRepository.searchMessagesInConversation(
            cuocTroChuyen, 
            request.getTuKhoa(), 
            pageable
        );
        
        // Chuyển đổi sang response
        List<GuiTinNhanResponse> responses = new ArrayList<>();
        for (TinNhan tinNhan : tinNhans.getContent()) {
            GuiTinNhanResponse response = new GuiTinNhanResponse();
            response.setIdTinNhan(tinNhan.getId());
            response.setIdCuocTroChuyen(cuocTroChuyen.getId());
            response.setIdNguoiGui(tinNhan.getNguoiGui().getId());
            response.setNoiDung(tinNhan.getNoiDung());
            response.setLoaiTinNhan(tinNhan.getLoaiTinNhan().name());
            response.setUrlTepTin(tinNhan.getUrlTepTin());
            response.setDaDoc(tinNhan.getDaDoc());
            response.setNgayTao(tinNhan.getNgayTao());
            // Bổ sung tên và avatar người gửi
            response.setTenNguoiGui(tinNhan.getNguoiGui().getHoTen());
            if (tinNhan.getNguoiGui().getAnhDaiDien() != null && !tinNhan.getNguoiGui().getAnhDaiDien().isEmpty()) {
                response.setAnhNguoiGui(tinNhan.getNguoiGui().getAnhDaiDien().get(0).getUrl());
            } else {
                response.setAnhNguoiGui(null);
            }
            // Bổ sung danh sách người đã đọc nếu là nhóm
            if (isGroup) {
                List<TinNhanDaDoc> daDocList = tinNhanDaDocRepository.findByTinNhan(tinNhan);
                List<NguoiDocDTO> nguoiDocDTOs = daDocList.stream().map(daDoc -> NguoiDocDTO.builder()
                    .id(daDoc.getNguoiDoc().getId())
                    .hoTen(daDoc.getNguoiDoc().getHoTen())
                    .anhDaiDien((daDoc.getNguoiDoc().getAnhDaiDien() != null && !daDoc.getNguoiDoc().getAnhDaiDien().isEmpty()) ? daDoc.getNguoiDoc().getAnhDaiDien().get(0).getUrl() : null)
                    .build()
                ).collect(Collectors.toList());
                response.setDanhSachNguoiDoc(nguoiDocDTOs);
            }
            responses.add(response);
        }
        
        return responses;
    }
    @Override
public List<TaoCuocTroChuyenResponse> layDanhSachCuocTroChuyen(Integer idNguoiDung) {
    NguoiDung nguoiDung = nguoiDungRepository.findById(idNguoiDung)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

    // Lấy tất cả các cuộc trò chuyện mà user là thành viên
    List<ThanhVienCuocTroChuyen> thanhVienList = thanhVienCuocTroChuyenRepository.findByNguoiDung(nguoiDung);
    List<TaoCuocTroChuyenResponse> result = new ArrayList<>();
    for (ThanhVienCuocTroChuyen tv : thanhVienList) {
        CuocTroChuyen cuoc = tv.getCuocTroChuyen();
        // Lấy tin nhắn cuối cùng
        TinNhan lastMsg = tinNhanRepository.findByCuocTroChuyenOrderByNgayTaoDesc(cuoc, org.springframework.data.domain.PageRequest.of(0, 1))
            .stream().findFirst().orElse(null);
        String lastContent = lastMsg != null ? lastMsg.getNoiDung() : null;
        String lastType = lastMsg != null && lastMsg.getLoaiTinNhan() != null ? lastMsg.getLoaiTinNhan().name() : null;
        Integer lastSenderId = lastMsg != null && lastMsg.getNguoiGui() != null ? lastMsg.getNguoiGui().getId() : null;
        String lastSenderName = lastMsg != null && lastMsg.getNguoiGui() != null ? lastMsg.getNguoiGui().getHoTen() : null;
        LocalDateTime lastTime = lastMsg != null ? lastMsg.getNgayTao() : null;
        // Đếm số tin nhắn chưa đọc
        long unreadCount;
        if (cuoc.getLoai().name().equals("nhom")) {
            unreadCount = tinNhanDaDocRepository.countUnreadGroupMessagesByConversation(cuoc, nguoiDung);
        } else {
            unreadCount = tinNhanRepository.countUnreadMessagesByConversation(cuoc, nguoiDung);
        }
        // Nếu là chat cá nhân, lấy thông tin đối phương
        if (cuoc.getLoai().name().equals("ca_nhan")) {
            List<ThanhVienCuocTroChuyen> members = thanhVienCuocTroChuyenRepository.findByCuocTroChuyen(cuoc);
            NguoiDung doiPhuong = members.stream()
                .map(ThanhVienCuocTroChuyen::getNguoiDung)
                .filter(nd -> !nd.getId().equals(idNguoiDung))
                .findFirst().orElse(null);
            result.add(TaoCuocTroChuyenResponse.builder()
                .idCuocTroChuyen(cuoc.getId())
                .loai(cuoc.getLoai().name())
                .tenNhom(null)
                .anhNhom(null)
                .idNguoiTao(cuoc.getNguoiTao() != null ? cuoc.getNguoiTao().getId() : null)
                .idThanhVien(members.stream().map(m -> m.getNguoiDung().getId()).toList())
                .idDoiPhuong(doiPhuong != null ? doiPhuong.getId() : null)
                .tenDoiPhuong(doiPhuong != null ? doiPhuong.getHoTen() : null)
                .anhDaiDienDoiPhuong(
                    (doiPhuong != null && doiPhuong.getAnhDaiDien() != null && !doiPhuong.getAnhDaiDien().isEmpty())
                        ? doiPhuong.getAnhDaiDien().get(0).getUrl()
                        : null
                )
                .tinNhanCuoi(cuoc.getTinNhanCuoi())
                .lastMessageContent(lastContent)
                .lastMessageType(lastType)
                .lastMessageSenderId(lastSenderId)
                .lastMessageSenderName(lastSenderName)
                .lastMessageTime(lastTime)
                .unreadCount(unreadCount)
                .build());
        } else {
            // Nếu là nhóm
            List<ThanhVienCuocTroChuyen> members = thanhVienCuocTroChuyenRepository.findByCuocTroChuyen(cuoc);
            // Danh sách thành viên chi tiết
            List<NguoiDungDTO> danhSachThanhVien = members.stream().map(thanhVien -> NguoiDungDTO.builder()
                .id(thanhVien.getNguoiDung().getId())
                .hoTen(thanhVien.getNguoiDung().getHoTen())
                .anhDaiDien((thanhVien.getNguoiDung().getAnhDaiDien() != null && !thanhVien.getNguoiDung().getAnhDaiDien().isEmpty()) ? thanhVien.getNguoiDung().getAnhDaiDien().get(0).getUrl() : null)
                .build()
            ).toList();
            Integer truongNhomId = cuoc.getNguoiTao() != null ? cuoc.getNguoiTao().getId() : null;
            result.add(TaoCuocTroChuyenResponse.builder()
                .idCuocTroChuyen(cuoc.getId())
                .loai(cuoc.getLoai().name())
                .tenNhom(cuoc.getTenNhom())
                .anhNhom(cuoc.getAnhNhom())
                .idNguoiTao(truongNhomId)
                .idThanhVien(members.stream().map(m -> m.getNguoiDung().getId()).toList())
                .danhSachThanhVien(danhSachThanhVien)
                .idTruongNhom(truongNhomId)
                .tinNhanCuoi(cuoc.getTinNhanCuoi())
                .lastMessageContent(lastContent)
                .lastMessageType(lastType)
                .lastMessageSenderId(lastSenderId)
                .lastMessageSenderName(lastSenderName)
                .lastMessageTime(lastTime)
                .unreadCount(unreadCount)
                .build());
        }
    }
    // Sắp xếp giảm dần theo tinNhanCuoi (null sẽ ở cuối)
    result.sort((a, b) -> {
        if (a.getTinNhanCuoi() == null && b.getTinNhanCuoi() == null) return 0;
        if (a.getTinNhanCuoi() == null) return 1;
        if (b.getTinNhanCuoi() == null) return -1;
        return b.getTinNhanCuoi().compareTo(a.getTinNhanCuoi());
    });
    return result;
}

@Override
public void markMessagesAsRead(Integer idCuocTroChuyen, Integer idNguoiDoc) {
    List<TinNhan> messages = tinNhanRepository.findByCuocTroChuyenIdAndNguoiGuiIdNotAndDaDocFalse(idCuocTroChuyen, idNguoiDoc);
    for (TinNhan msg : messages) {
        msg.setDaDoc(true);
        tinNhanRepository.save(msg);
    }
}

    @Override
    @Transactional
    public void kickMemberFromGroup(Integer idCuocTroChuyen, Integer idNguoiBiXoa, Integer idNguoiThucHien) {
        CuocTroChuyen cuocTroChuyen = cuocTroChuyenRepository.findById(idCuocTroChuyen)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc trò chuyện"));
        if (cuocTroChuyen.getLoai() != LoaiCuocTroChuyen.nhom) {
            throw new RuntimeException("Chỉ có thể xóa thành viên khỏi nhóm");
        }
        // Kiểm tra quyền
        ThanhVienCuocTroChuyen nguoiThucHien = thanhVienCuocTroChuyenRepository
            .findByCuocTroChuyenAndNguoiDung(cuocTroChuyen, nguoiDungRepository.findById(idNguoiThucHien)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người thực hiện")))
            .orElseThrow(() -> new RuntimeException("Người thực hiện không phải thành viên nhóm"));
        boolean isAdmin = nguoiThucHien.getVaiTro() == VaiTroThanhVien.quan_tri;
        boolean isCreator = cuocTroChuyen.getNguoiTao() != null && cuocTroChuyen.getNguoiTao().getId().equals(idNguoiThucHien);
        if (!isAdmin && !isCreator) {
            throw new RuntimeException("Chỉ trưởng nhóm hoặc quản trị viên mới có quyền xóa thành viên");
        }
        // Không cho xóa chính mình bằng API này
        if (idNguoiBiXoa.equals(idNguoiThucHien)) {
            throw new RuntimeException("Không thể tự xóa chính mình, hãy dùng chức năng rời nhóm");
        }
        NguoiDung nguoiBiXoa = nguoiDungRepository.findById(idNguoiBiXoa)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy thành viên bị xóa"));
        if (!thanhVienCuocTroChuyenRepository.existsByCuocTroChuyenAndNguoiDung(cuocTroChuyen, nguoiBiXoa)) {
            throw new RuntimeException("Người này không phải thành viên nhóm");
        }
        thanhVienCuocTroChuyenRepository.deleteByCuocTroChuyenAndNguoiDung(cuocTroChuyen, nguoiBiXoa);
        // Cập nhật số thành viên
        long soThanhVienHienTai = thanhVienCuocTroChuyenRepository.countByCuocTroChuyen(cuocTroChuyen);
        cuocTroChuyen.setSoThanhVien((int) soThanhVienHienTai);
        cuocTroChuyenRepository.save(cuocTroChuyen);

        // Tạo tin nhắn thông báo khi xóa thành viên
        TinNhan thongBao = new TinNhan();
        thongBao.setCuocTroChuyen(cuocTroChuyen);
        thongBao.setNguoiGui(nguoiThucHien.getNguoiDung()); // Người thực hiện hành động
        thongBao.setNoiDung(nguoiBiXoa.getHoTen() + " đã bị xóa khỏi nhóm");
        thongBao.setLoaiTinNhan(LoaiTinNhan.thong_bao);
        thongBao.setNgayTao(LocalDateTime.now());
        thongBao.setDaDoc(false);
        tinNhanRepository.save(thongBao);
    }

    @Override
    @Transactional
    public void leaveGroup(Integer idCuocTroChuyen, Integer idNguoiRoi) {
        CuocTroChuyen cuocTroChuyen = cuocTroChuyenRepository.findById(idCuocTroChuyen)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc trò chuyện"));
        if (cuocTroChuyen.getLoai() != LoaiCuocTroChuyen.nhom) {
            throw new RuntimeException("Chỉ có thể rời nhóm");
        }
        NguoiDung nguoiRoi = nguoiDungRepository.findById(idNguoiRoi)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người rời nhóm"));
        // Không cho trưởng nhóm rời nhóm nếu là người duy nhất
        if (cuocTroChuyen.getNguoiTao() != null && cuocTroChuyen.getNguoiTao().getId().equals(idNguoiRoi)) {
            long soThanhVien = thanhVienCuocTroChuyenRepository.countByCuocTroChuyen(cuocTroChuyen);
            if (soThanhVien <= 1) {
                throw new RuntimeException("Trưởng nhóm không thể rời nhóm khi là thành viên duy nhất. Hãy xóa nhóm hoặc chuyển quyền trưởng nhóm.");
            }
            // TODO: Có thể chuyển quyền trưởng nhóm cho người khác trước khi rời
        }
        if (!thanhVienCuocTroChuyenRepository.existsByCuocTroChuyenAndNguoiDung(cuocTroChuyen, nguoiRoi)) {
            throw new RuntimeException("Bạn không phải thành viên nhóm");
        }
        thanhVienCuocTroChuyenRepository.deleteByCuocTroChuyenAndNguoiDung(cuocTroChuyen, nguoiRoi);
        // Cập nhật số thành viên
        long soThanhVienHienTai = thanhVienCuocTroChuyenRepository.countByCuocTroChuyen(cuocTroChuyen);
        cuocTroChuyen.setSoThanhVien((int) soThanhVienHienTai);
        cuocTroChuyenRepository.save(cuocTroChuyen);

        // Tạo tin nhắn thông báo khi rời nhóm
        TinNhan thongBao = new TinNhan();
        thongBao.setCuocTroChuyen(cuocTroChuyen);
        thongBao.setNguoiGui(nguoiRoi); // hoặc null nếu muốn
        thongBao.setNoiDung(nguoiRoi.getHoTen() + " đã rời nhóm");
        thongBao.setLoaiTinNhan(LoaiTinNhan.thong_bao); // hoặc LoaiTinNhan.he_thong nếu enum của bạn là vậy
        thongBao.setNgayTao(LocalDateTime.now());
        thongBao.setDaDoc(false);
        tinNhanRepository.save(thongBao);
    }

    @Override
    @Transactional
    public void xoaNhom(Integer idCuocTroChuyen, Integer idNguoiThucHien) {
        CuocTroChuyen cuocTroChuyen = cuocTroChuyenRepository.findById(idCuocTroChuyen)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc trò chuyện"));
        if (cuocTroChuyen.getLoai() != LoaiCuocTroChuyen.nhom) {
            throw new RuntimeException("Chỉ có thể xóa nhóm");
        }
        if (cuocTroChuyen.getNguoiTao() == null || !cuocTroChuyen.getNguoiTao().getId().equals(idNguoiThucHien)) {
            throw new RuntimeException("Chỉ trưởng nhóm mới có quyền xóa nhóm");
        }
        // Xóa tất cả thành viên nhóm
        thanhVienCuocTroChuyenRepository.deleteAllByCuocTroChuyen(cuocTroChuyen);
        // Xóa tất cả tin nhắn của nhóm (nếu muốn)
        tinNhanRepository.deleteAllByCuocTroChuyen(cuocTroChuyen);
        // Xóa nhóm
        cuocTroChuyenRepository.delete(cuocTroChuyen);
    }
}

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
            var existing = cuocTroChuyenRepository.findPrivateConversationBetweenUsers(nguoi1, nguoi2);
            if (existing.isPresent()) {
                CuocTroChuyen cuoc = existing.get();
                return TaoCuocTroChuyenResponse.builder()
                    .idCuocTroChuyen(cuoc.getId())
                    .loai(cuoc.getLoai().name())
                    .tenNhom(cuoc.getTenNhom())
                    .anhNhom(cuoc.getAnhNhom())
                    .idNguoiTao(cuoc.getNguoiTao() != null ? cuoc.getNguoiTao().getId() : null)
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
        }
        
        // Cập nhật số thành viên
        long soThanhVienHienTai = thanhVienCuocTroChuyenRepository.countByCuocTroChuyen(cuocTroChuyen);
        cuocTroChuyen.setSoThanhVien((int) soThanhVienHienTai);
        cuocTroChuyenRepository.save(cuocTroChuyen);
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
            // Chỉ trả về tin nhắn chưa bị thu hồi
            if (tinNhan.getDaXoa() == null || !tinNhan.getDaXoa()) {
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
}

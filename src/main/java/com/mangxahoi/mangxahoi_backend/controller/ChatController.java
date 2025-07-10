package com.mangxahoi.mangxahoi_backend.controller;

import com.mangxahoi.mangxahoi_backend.dto.*;
import com.mangxahoi.mangxahoi_backend.dto.request.*;
import com.mangxahoi.mangxahoi_backend.dto.response.*;
import com.mangxahoi.mangxahoi_backend.service.ChatService;
import com.mangxahoi.mangxahoi_backend.service.CloudinaryService;
import com.mangxahoi.mangxahoi_backend.util.TokenUtil;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.mangxahoi.mangxahoi_backend.service.TinNhanDaDocService;
import com.mangxahoi.mangxahoi_backend.repository.TinNhanRepository;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import com.mangxahoi.mangxahoi_backend.entity.TinNhan;

@RestController
@RequestMapping("/api/tinnhan")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private TinNhanDaDocService tinNhanDaDocService;
    @Autowired
    private TinNhanRepository tinNhanRepository;
    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @PostMapping("/gui")
    public GuiTinNhanResponse guiTinNhan(
        @RequestHeader("Authorization") String authorization,
        @RequestBody GuiTinNhanRequest request
    ) {
        System.out.println("loaiTinNhan nhận được: " + request.getLoaiTinNhan());
        NguoiDung nguoiGui = getUserFromToken(authorization);
        request.setIdNguoiGui(nguoiGui.getId());
        return chatService.guiTinNhan(request);
    }

    @PostMapping("/cuoc-tro-chuyen/tao")
    public TaoCuocTroChuyenResponse taoCuocTroChuyen(
        @RequestHeader("Authorization") String authorization,
        @RequestBody TaoCuocTroChuyenRequest request
    ) {
        NguoiDung nguoiTao = getUserFromToken(authorization);
        request.setIdNguoiTao(nguoiTao.getId());
        return chatService.taoCuocTroChuyen(request);
    }

    @PostMapping("/cuoc-tro-chuyen/them-thanh-vien")
    public void themThanhVien(
        @RequestHeader("Authorization") String authorization,
        @RequestBody ThemThanhVienRequest request
    ) {
        NguoiDung nguoiThucHien = getUserFromToken(authorization);
        request.setIdNguoiThucHien(nguoiThucHien.getId());
        chatService.themThanhVien(request);
    }

    @PostMapping("/tin-nhan/thu-hoi")
    public void thuHoiTinNhan(
        @RequestHeader("Authorization") String authorization,
        @RequestBody ThuHoiTinNhanRequest request
    ) {
        NguoiDung nguoiThucHien = getUserFromToken(authorization);
        request.setIdNguoiThucHien(nguoiThucHien.getId());
        chatService.thuHoiTinNhan(request);
    }

    @PostMapping("/tin-nhan/tim-kiem")
    public List<GuiTinNhanResponse> timKiemTinNhan(
        @RequestHeader("Authorization") String authorization,
        @RequestBody TimKiemTinNhanRequest request) {
             NguoiDung nguoiThucHien = getUserFromToken(authorization);
        request.setIdNguoiThucHien(nguoiThucHien.getId());
        return chatService.timKiemTinNhan(request);
    }

    @PostMapping("/tin-nhan/upload-file")
    public ResponseEntity<?> uploadTinNhanFile(
        @RequestHeader("Authorization") String authorization,
        @RequestParam("file") MultipartFile file
    ) {
        // Kiểm tra token
        getUserFromToken(authorization);
        try {
            String url = cloudinaryService.uploadFile(file, "tin_nhan");
            return ResponseEntity.ok().body(Map.of("url", url));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/danh-dau-da-doc")
    public ResponseEntity<?> markMessagesAsRead(
        @RequestHeader("Authorization") String authorization,
        @RequestBody MarkAsReadRequest request
    ) {
        NguoiDung nguoiDung = getUserFromToken(authorization);
        request.setIdNguoiDoc(nguoiDung.getId());
        chatService.markMessagesAsRead(request.getIdCuocTroChuyen(), request.getIdNguoiDoc());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/nhom/danh-dau-da-doc")
    public ResponseEntity<?> markGroupMessageAsRead(
        @RequestHeader("Authorization") String authorization,
        @RequestBody MarkAsReadRequest request
    ) {
        NguoiDung nguoiDoc = getUserFromToken(authorization);
        // Lấy danh sách tin nhắn cần đánh dấu đã đọc (ví dụ: tất cả tin nhắn chưa đọc trong nhóm)
        List<TinNhan> messages = tinNhanRepository.findByCuocTroChuyenIdAndNguoiGuiIdNotAndDaDocFalse(
            request.getIdCuocTroChuyen(), nguoiDoc.getId()
        );
        for (TinNhan msg : messages) {
            tinNhanDaDocService.danhDauDaDoc(msg, nguoiDoc);
        }
        return ResponseEntity.ok().build();
    }
    @GetMapping("/cuoc-tro-chuyen/danh-sach")
public List<TaoCuocTroChuyenResponse> layDanhSachCuocTroChuyen(
    @RequestHeader("Authorization") String authorization
) {
    NguoiDung nguoiDung = getUserFromToken(authorization);
    return chatService.layDanhSachCuocTroChuyen(nguoiDung.getId());
}

    private NguoiDung getUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SecurityException("Invalid or missing Authorization header");
        }
        String token = authHeader.substring(7);
        return tokenUtil.layNguoiDungTuToken(token);
    }
}

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
import com.mangxahoi.mangxahoi_backend.repository.CuocTroChuyenRepository;
import com.mangxahoi.mangxahoi_backend.entity.CuocTroChuyen;

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
    @Autowired
    private CuocTroChuyenRepository cuocTroChuyenRepository;

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

    @PostMapping("/tin-nhan/xoa")
    public ResponseEntity<?> xoaTinNhan(
        @RequestHeader("Authorization") String authorization,
        @RequestBody Map<String, Integer> request
    ) {
        NguoiDung nguoiThucHien = getUserFromToken(authorization);
        Integer idTinNhan = request.get("idTinNhan");
        Integer idCuocTroChuyen = request.get("idCuocTroChuyen");
        chatService.xoaTinNhan(idTinNhan, idCuocTroChuyen, nguoiThucHien.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tin-nhan/xoa-toan-bo")
    public ResponseEntity<?> xoaToanBoTinNhan(
        @RequestHeader("Authorization") String authorization,
        @RequestBody Map<String, Integer> request
    ) {
        NguoiDung nguoiThucHien = getUserFromToken(authorization);
        Integer idCuocTroChuyen = request.get("idCuocTroChuyen");
        chatService.xoaToanBoTinNhan(idCuocTroChuyen, nguoiThucHien.getId());
        return ResponseEntity.ok().build();
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
        CuocTroChuyen cuocTroChuyen = cuocTroChuyenRepository.findById(request.getIdCuocTroChuyen()).orElse(null);
        if (cuocTroChuyen == null) return ResponseEntity.badRequest().body("Cuộc trò chuyện không tồn tại");
        List<TinNhan> allMessages = tinNhanRepository.findByCuocTroChuyen(cuocTroChuyen);
        for (TinNhan msg : allMessages) {
            if (!tinNhanDaDocService.daDoc(msg, nguoiDoc)) {
                tinNhanDaDocService.danhDauDaDoc(msg, nguoiDoc);
            }
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

    @PostMapping("/cuoc-tro-chuyen/xoa-thanh-vien")
    public ResponseEntity<?> kickMemberFromGroup(
        @RequestHeader("Authorization") String authorization,
        @RequestBody Map<String, Integer> body
    ) {
        NguoiDung nguoiThucHien = getUserFromToken(authorization);
        Integer idCuocTroChuyen = body.get("idCuocTroChuyen");
        Integer idNguoiBiXoa = body.get("idNguoiBiXoa");
        chatService.kickMemberFromGroup(idCuocTroChuyen, idNguoiBiXoa, nguoiThucHien.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cuoc-tro-chuyen/roi-nhom")
    public ResponseEntity<?> leaveGroup(
        @RequestHeader("Authorization") String authorization,
        @RequestBody Map<String, Integer> body
    ) {
        NguoiDung nguoiRoi = getUserFromToken(authorization);
        Integer idCuocTroChuyen = body.get("idCuocTroChuyen");
        chatService.leaveGroup(idCuocTroChuyen, nguoiRoi.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cuoc-tro-chuyen/xoa-nhom")
    public ResponseEntity<?> xoaNhom(
        @RequestHeader("Authorization") String authorization,
        @RequestBody Map<String, Integer> body
    ) {
        NguoiDung nguoiThucHien = getUserFromToken(authorization);
        Integer idCuocTroChuyen = body.get("idCuocTroChuyen");
        chatService.xoaNhom(idCuocTroChuyen, nguoiThucHien.getId());
        return ResponseEntity.ok().build();
    }

    private NguoiDung getUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SecurityException("Invalid or missing Authorization header");
        }
        String token = authHeader.substring(7);
        return tokenUtil.layNguoiDungTuToken(token);
    }
}

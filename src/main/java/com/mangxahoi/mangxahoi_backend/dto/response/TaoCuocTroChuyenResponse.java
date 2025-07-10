package com.mangxahoi.mangxahoi_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaoCuocTroChuyenResponse {
    private Integer idCuocTroChuyen;
    private String loai;
    private String tenNhom;
    private String anhNhom;
    private Integer idNguoiTao;
    private List<Integer> idThanhVien;
    private Integer idDoiPhuong;
    private String tenDoiPhuong;
    private String anhDaiDienDoiPhuong;

    private LocalDateTime tinNhanCuoi;

    // Thông tin tin nhắn cuối cùng
    private String lastMessageContent;
    private String lastMessageType;
    private Integer lastMessageSenderId;
    private String lastMessageSenderName;
    private LocalDateTime lastMessageTime;

    // Số tin nhắn chưa đọc đối với user hiện tại
    private long unreadCount;
}

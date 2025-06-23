package com.mangxahoi.mangxahoi_backend.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
    private final String errorCode;
    
    public AuthException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public AuthException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    // Các mã lỗi phổ biến cho xác thực
    public static final String EMAIL_DA_TON_TAI = "AUTH_001";
    public static final String SDT_DA_TON_TAI = "AUTH_002";
    public static final String EMAIL_SDT_KHONG_TON_TAI = "AUTH_003";
    public static final String MAT_KHAU_KHONG_DUNG = "AUTH_004";
    public static final String TAI_KHOAN_BI_KHOA = "AUTH_005";
    public static final String TAI_KHOAN_BI_XOA = "AUTH_006";
    public static final String TOKEN_KHONG_HOP_LE = "AUTH_007";
    public static final String XAC_THUC_THAT_BAI = "AUTH_008";
} 
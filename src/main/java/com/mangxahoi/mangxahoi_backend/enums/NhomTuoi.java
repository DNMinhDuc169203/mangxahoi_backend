package com.mangxahoi.mangxahoi_backend.enums;

public enum NhomTuoi {
    T18_25("18-25"),
    T26_35("26-35"),
    T36_45("36-45"),
    T46_55("46-55"),
    T55_PLUS("55+");
    
    private final String value;
    
    NhomTuoi(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
} 
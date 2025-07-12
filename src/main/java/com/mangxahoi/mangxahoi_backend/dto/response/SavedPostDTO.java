package com.mangxahoi.mangxahoi_backend.dto.response;

import com.mangxahoi.mangxahoi_backend.dto.BaiVietDTO;

public class SavedPostDTO {
    private Integer id;
    private BaiVietDTO baiViet;
    private String savedAt;

    public SavedPostDTO() {}
    public SavedPostDTO(Integer id, BaiVietDTO baiViet, String savedAt) {
        this.id = id;
        this.baiViet = baiViet;
        this.savedAt = savedAt;
    }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public BaiVietDTO getBaiViet() { return baiViet; }
    public void setBaiViet(BaiVietDTO baiViet) { this.baiViet = baiViet; }
    public String getSavedAt() { return savedAt; }
    public void setSavedAt(String savedAt) { this.savedAt = savedAt; }
} 
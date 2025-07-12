package com.mangxahoi.mangxahoi_backend.service;

import com.mangxahoi.mangxahoi_backend.entity.BaiViet;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.SavedPost;

import java.util.List;

public interface SavedPostService {
    void savePost(Long userId, Long postId);
    void unsavePost(Long userId, Long postId);
    List<SavedPost> getSavedPostsByUser(Long userId);
} 
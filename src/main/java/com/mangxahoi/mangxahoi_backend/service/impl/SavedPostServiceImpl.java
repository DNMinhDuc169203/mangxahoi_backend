package com.mangxahoi.mangxahoi_backend.service.impl;

import com.mangxahoi.mangxahoi_backend.entity.BaiViet;
import com.mangxahoi.mangxahoi_backend.entity.NguoiDung;
import com.mangxahoi.mangxahoi_backend.entity.SavedPost;
import com.mangxahoi.mangxahoi_backend.repository.BaiVietRepository;
import com.mangxahoi.mangxahoi_backend.repository.NguoiDungRepository;
import com.mangxahoi.mangxahoi_backend.repository.SavedPostRepository;
import com.mangxahoi.mangxahoi_backend.service.SavedPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SavedPostServiceImpl implements SavedPostService {
    @Autowired
    private SavedPostRepository savedPostRepository;
    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    @Autowired
    private BaiVietRepository baiVietRepository;

    @Override
    @Transactional
    public void savePost(Long userId, Long postId) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(userId.intValue()).orElseThrow(() -> new RuntimeException("User not found"));
        BaiViet baiViet = baiVietRepository.findById(postId.intValue()).orElseThrow(() -> new RuntimeException("Post not found"));
        if (savedPostRepository.findByNguoiDungAndBaiViet(nguoiDung, baiViet).isPresent()) {
            return; // Đã lưu rồi
        }
        SavedPost savedPost = new SavedPost();
        savedPost.setNguoiDung(nguoiDung);
        savedPost.setBaiViet(baiViet);
        savedPostRepository.save(savedPost);
    }

    @Override
    @Transactional
    public void unsavePost(Long userId, Long postId) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(userId.intValue()).orElseThrow(() -> new RuntimeException("User not found"));
        BaiViet baiViet = baiVietRepository.findById(postId.intValue()).orElseThrow(() -> new RuntimeException("Post not found"));
        savedPostRepository.deleteByNguoiDungAndBaiViet(nguoiDung, baiViet);
    }

    @Override
    public List<SavedPost> getSavedPostsByUser(Long userId) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(userId.intValue()).orElseThrow(() -> new RuntimeException("User not found"));
        return savedPostRepository.findByNguoiDung(nguoiDung);
    }
} 
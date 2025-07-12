package com.mangxahoi.mangxahoi_backend.controller;

import com.mangxahoi.mangxahoi_backend.entity.SavedPost;
import com.mangxahoi.mangxahoi_backend.service.SavedPostService;
import com.mangxahoi.mangxahoi_backend.dto.response.SavedPostDTO;
import com.mangxahoi.mangxahoi_backend.dto.BaiVietDTO;
import com.mangxahoi.mangxahoi_backend.service.BaiVietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-posts")
public class SavedPostController {
    @Autowired
    private SavedPostService savedPostService;
    @Autowired
    private BaiVietService baiVietService;

    @PostMapping("/save")
    public ResponseEntity<?> savePost(@RequestParam Long userId, @RequestParam Long postId) {
        savedPostService.savePost(userId, postId);
        return ResponseEntity.ok().body("Saved");
    }

    @DeleteMapping("/unsave")
    public ResponseEntity<?> unsavePost(@RequestParam Long userId, @RequestParam Long postId) {
        savedPostService.unsavePost(userId, postId);
        return ResponseEntity.ok().body("Unsaved");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SavedPostDTO>> getSavedPostsByUser(@PathVariable Long userId) {
        List<SavedPost> savedPosts = savedPostService.getSavedPostsByUser(userId);
        List<SavedPostDTO> result = savedPosts.stream().map(savedPost -> {
            BaiVietDTO baiVietDTO = baiVietService.timTheoId(savedPost.getBaiViet().getId(), savedPost.getNguoiDung().getId())
                .orElse(null); // hoặc truyền null nếu không cần phân quyền
            return new SavedPostDTO(
                savedPost.getId().intValue(),
                baiVietDTO,
                savedPost.getCreatedAt().toString()
            );
        }).toList();
        return ResponseEntity.ok(result);
    }
} 
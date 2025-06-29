package com.mangxahoi.mangxahoi_backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    
    /**
     * Upload file lên Cloudinary
     * 
     * @param file File cần upload
     * @param folder Thư mục lưu trữ trên Cloudinary
     * @return Đường dẫn của file đã upload
     * @throws IOException Nếu có lỗi khi upload
     */
    String uploadFile(MultipartFile file, String folder) throws IOException;
    
    /**
     * Upload file từ đường dẫn local lên Cloudinary
     * 
     * @param filePath Đường dẫn đến file cần upload
     * @param folder Thư mục lưu trữ trên Cloudinary
     * @return URL của file đã upload
     * @throws IOException Nếu có lỗi khi upload
     */
    String uploadFileFromPath(String filePath, String folder) throws IOException;
    
    /**
     * Xóa file trên Cloudinary
     * 
     * @param publicId Public ID của file cần xóa
     * @return Kết quả xóa file
     * @throws IOException Nếu có lỗi khi xóa
     */
    Map<?, ?> deleteFile(String publicId) throws IOException;
} 
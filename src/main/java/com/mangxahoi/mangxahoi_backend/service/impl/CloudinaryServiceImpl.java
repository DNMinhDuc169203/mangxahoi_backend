package com.mangxahoi.mangxahoi_backend.service.impl;

import com.cloudinary.Cloudinary;
import com.mangxahoi.mangxahoi_backend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        try {
            if (file == null || file.isEmpty()) {
                throw new IOException("File trống hoặc không tồn tại");
            }
            
            // Kiểm tra kích thước file
            if (file.getSize() > 10 * 1024 * 1024) { // 10MB
                throw new IOException("File quá lớn, kích thước tối đa là 10MB");
            }
            
            // Kiểm tra loại file
            String contentType = file.getContentType();
            if (contentType == null || 
                !(contentType.startsWith("image/") || contentType.startsWith("video/"))) {
                throw new IOException("Loại file không được hỗ trợ: " + contentType);
            }
            
            // Chuyển đổi MultipartFile sang File
            File uploadedFile = convertMultiPartToFile(file);
            
            // Tạo options cho việc upload
            Map<String, Object> params = new HashMap<>();
            params.put("folder", folder);
            params.put("resource_type", "auto"); // Tự động nhận diện loại tài nguyên (ảnh, video, ...)
            
            // Upload lên Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(uploadedFile, params);
            
            // Xóa file tạm
            uploadedFile.delete();
            
            // Trả về URL của file đã upload
            return uploadResult.get("url").toString();
        } catch (IOException e) {
            throw new IOException("Lỗi khi upload file: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new IOException("Lỗi không xác định khi upload file: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadFileFromPath(String filePath, String folder) throws IOException {
        try {
            // Tạo file từ đường dẫn
            File file = new File(filePath);
            if (!file.exists()) {
                throw new IOException("File không tồn tại: " + filePath);
            }
            
            // Tạo options cho việc upload
            Map<String, Object> params = new HashMap<>();
            params.put("folder", folder);
            params.put("resource_type", "auto"); // Tự động nhận diện loại tài nguyên (ảnh, video, ...)
            
            // Upload lên Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file, params);
            
            // Trả về URL của file đã upload
            return uploadResult.get("url").toString();
        } catch (IOException e) {
            throw new IOException("Lỗi khi upload file từ đường dẫn: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> deleteFile(String publicId) throws IOException {
        try {
            // Tạo options cho việc xóa
            Map<String, Object> params = new HashMap<>();
            params.put("invalidate", true); // Xóa cache
            
            // Xóa file trên Cloudinary
            Map<String, Object> deleteResult = cloudinary.uploader().destroy(publicId, params);
            
            return deleteResult;
        } catch (IOException e) {
            throw new IOException("Lỗi khi xóa file: " + e.getMessage());
        }
    }

    /**
     * Chuyển đổi MultipartFile sang File
     * 
     * @param file MultipartFile cần chuyển đổi
     * @return File đã chuyển đổi
     * @throws IOException Nếu có lỗi khi chuyển đổi
     */
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        // Tạo tên file tạm
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        File convertedFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
        
        // Ghi dữ liệu vào file tạm
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        
        return convertedFile;
    }
} 
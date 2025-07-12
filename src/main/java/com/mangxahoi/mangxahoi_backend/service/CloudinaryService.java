package com.mangxahoi.mangxahoi_backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {

    String uploadFile(MultipartFile file, String folder) throws IOException;
    String uploadFileFromPath(String filePath, String folder) throws IOException;

    Map<?, ?> deleteFile(String publicId) throws IOException;
} 
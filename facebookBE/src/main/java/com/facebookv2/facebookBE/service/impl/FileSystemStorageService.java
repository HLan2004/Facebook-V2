package com.facebookv2.facebookBE.service.impl;

import com.facebookv2.facebookBE.service.StorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service // Đánh dấu đây là một Bean để Spring quản lý
public class FileSystemStorageService implements StorageService {

    // Lấy giá trị từ file application.properties
    @Value("${upload.path}")
    private String uploadPath;

    @PostConstruct // Hàm này sẽ chạy ngay sau khi Service được tạo
    public void init() {
        try {
            // Tạo thư mục nếu nó chưa tồn tại
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        // Nếu không có file tải lên thì bỏ qua
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // Tạo một cái tên file ngẫu nhiên, duy nhất để tránh bị trùng lặp
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID().toString() + extension;

            // Đường dẫn đầy đủ tới file sẽ được lưu
            Path destinationFile = Paths.get(uploadPath).resolve(uniqueFileName)
                    .normalize().toAbsolutePath();

            // Copy nội dung file vào thư mục đích
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Trả về tên file duy nhất vừa tạo
            return uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }
}
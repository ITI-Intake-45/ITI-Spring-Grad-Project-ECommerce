package gov.iti.jet.ewd.ecom.service.impl;

import gov.iti.jet.ewd.ecom.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Service
public class FileStorageServiceImpl implements FileStorageService{

    private final Path uploadPath;


    public FileStorageServiceImpl(@Value("${spring.upload-dir}") String uploadDir) {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadPath); // Create the directory if it doesn’t exist
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            // Generate a unique file name to avoid conflicts (optional)
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path targetLocation = this.uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName; // Return the file name to store in the database
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }
}
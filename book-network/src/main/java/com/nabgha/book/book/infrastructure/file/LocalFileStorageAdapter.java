package com.nabgha.book.book.infrastructure.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class LocalFileStorageAdapter implements FileStoragePort {

    @Value("${spring.application.file.upload.photos-output-path}")
    private String fileUploadPath;

    @Override
    public String save(byte[] fileContent, String originalFilename, Integer ownerId) {
        final String subPath = "users" + File.separator + ownerId;
        final String finalUploadPath = fileUploadPath + File.separator + subPath;
        File targetFolder = new File(finalUploadPath);
        if (!targetFolder.exists() && !targetFolder.mkdirs()) {
            log.warn("Failed to create target folder {}", finalUploadPath);
            return null;
        }
        String extension = getExtension(originalFilename);
        String targetFilePath = finalUploadPath + File.separator + System.currentTimeMillis() + "." + extension;
        Path targetPath = Paths.get(targetFilePath);
        try {
            Files.write(targetPath, fileContent);
            return targetFilePath;
        } catch (IOException e) {
            log.error("Failed to upload file to {}", targetFilePath, e);
            return null;
        }
    }

    @Override
    public byte[] read(String location) {
        if (location == null || location.isBlank()) {
            return null;
        }
        try {
            return Files.readAllBytes(Paths.get(location));
        } catch (IOException e) {
            log.warn("No file found at {}", location, e);
            return null;
        }
    }

    private String getExtension(String filename) {
        if (filename == null || filename.isEmpty()) return "";
        int i = filename.lastIndexOf('.');
        return i == -1 ? "" : filename.substring(i + 1).toLowerCase();
    }

}

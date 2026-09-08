package com.nabgha.book.book.infrastructure.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class S3FileStorageAdapter implements FileStoragePort {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Override
    public String save(byte[] fileContent, String originalFilename, Integer ownerId) {
        String extension = getExtension(originalFilename);
        String s3Key = "users/" + ownerId + "/" + System.currentTimeMillis() + (extension.isEmpty() ? "" : "." + extension);
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileContent));
            log.info("Successfully uploaded book cover to S3: {} in bucket {}", s3Key, bucketName);
            return s3Key;
        } catch (Exception e) {
            log.error("Failed to upload book cover to S3 bucket {} with key {}", bucketName, s3Key, e);
            return null;
        }
    }

    @Override
    public byte[] read(String location) {
        if (location == null || location.isBlank()) {
            return null;
        }
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(location)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            return objectBytes.asByteArray();
        } catch (NoSuchKeyException e) {
            log.warn("No S3 object found in bucket {} for key {}", bucketName, location);
            return null;
        } catch (Exception e) {
            log.error("Error reading S3 object from bucket {} for key {}", bucketName, location, e);
            return null;
        }
    }

    private String getExtension(String filename) {
        if (filename == null || filename.isEmpty()) return "";
        int i = filename.lastIndexOf('.');
        return i == -1 ? "" : filename.substring(i + 1).toLowerCase();
    }
}

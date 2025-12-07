package com.deulbull.performance.global.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file, String keyPrefix) {
        String ext = getExtension(file.getOriginalFilename()).toLowerCase();
        String key = keyPrefix + "-" + UUID.randomUUID() + "." + ext;

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .cacheControl("max-age=2592000, public")
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }

        return "https://" + bucket + ".s3.amazonaws.com/" + key;
    }

    public void delete(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);
        if (key.isEmpty()) return;

        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build()
            );
        } catch (Exception ignored) {}
    }

    public void deleteFolder(String prefix) {
        ListObjectsV2Response list = s3Client.listObjectsV2(
                ListObjectsV2Request.builder()
                        .bucket(bucket)
                        .prefix(prefix)
                        .build()
        );

        for (S3Object obj : list.contents()) {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(obj.key())
                            .build()
            );
        }
    }

    private String extractKeyFromUrl(String fileUrl) {
        if (fileUrl == null) return "";
        String[] parts = fileUrl.split(bucket + ".s3.amazonaws.com/");
        return parts.length > 1 ? parts[1] : "";
    }

    private String getExtension(String filename) {
        if (filename == null) return "dat";
        int idx = filename.lastIndexOf('.');
        return (idx == -1) ? "dat" : filename.substring(idx + 1);
    }
}

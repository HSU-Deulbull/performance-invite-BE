package com.deulbull.performance.global.s3;

import com.luciad.imageio.webp.WebPWriteParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file, String keyPrefix) {

        try {
            // 1) MultipartFile → BufferedImage
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new RuntimeException("이미지 변환 실패: 지원되지 않는 포맷");
            }

            // 2) WebP로 변환
            byte[] webpBytes = convertToWebP(image, 0.9f); // 90% 품질

            // 3) 파일명 .webp로 저장
            String key = keyPrefix + "-" + UUID.randomUUID() + ".webp";

            // 4) S3 업로드
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType("image/webp")
                            .cacheControl("max-age=2592000, public")
                            .build(),
                    RequestBody.fromBytes(webpBytes)
            );

            // 5) 반환 URL
            return "https://" + bucket + ".s3.amazonaws.com/" + key;

        } catch (IOException e) {
            throw new RuntimeException("WebP 변환 또는 S3 업로드 실패", e);
        }
    }

    // S3에서 파일 삭제
    public void delete(String fileUrl) {
        try {
            // URL에서 키 추출
            String key = extractKeyFromUrl(fileUrl);

            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build()
            );

            System.out.println("[S3 삭제 성공] key=" + key);
        } catch (Exception e) {
            System.out.println("[S3 삭제 실패] url=" + fileUrl + ", error=" + e.getMessage());
            // 삭제 실패해도 예외를 던지지 않음 (이미 삭제된 파일일 수 있음)
        }
    }

    // URL에서 S3 키 추출
    private String extractKeyFromUrl(String fileUrl) {
        // https://bucket-name.s3.amazonaws.com/key 형식에서 key 추출
        if (fileUrl == null || fileUrl.isEmpty()) {
            return "";
        }

        String[] parts = fileUrl.split(bucket + ".s3.amazonaws.com/");
        if (parts.length > 1) {
            return parts[1];
        }

        return "";
    }

    // WebP 변환 함수
    private byte[] convertToWebP(BufferedImage image, float quality) throws IOException {

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/webp");
        if (!writers.hasNext()) {
            throw new IllegalStateException("WebP ImageWriter를 찾을 수 없습니다.");
        }

        ImageWriter writer = writers.next();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream(baos);
        writer.setOutput(output);

        WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());
        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        writeParam.setCompressionQuality(quality); // 0.0 ~ 1.0

        writer.write(null, new IIOImage(image, null, null), writeParam);
        writer.dispose();

        return baos.toByteArray();
    }
}

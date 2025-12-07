package com.deulbull.performance.global.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

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

        String originalExt = getExtension(file.getOriginalFilename()).toLowerCase();
        long fileSize = file.getSize();

        byte[] finalBytes;
        String finalExt = originalExt;

        try {
            // PNG가 너무 클 때만 JPEG로 변환
            if (originalExt.equals("png") && fileSize > 1_000_000) { // 1MB 이상
                BufferedImage image = ImageIO.read(file.getInputStream());
                finalBytes = convertPngToJpeg(image, 0.9f); // 품질 90%
                finalExt = "jpg";
            } else {
                // 변환 없이 그대로 사용
                finalBytes = file.getBytes();
            }

            // S3 저장 키
            String key = keyPrefix + "-" + UUID.randomUUID() + "." + finalExt;

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType("image/" + finalExt)
                            .cacheControl("max-age=2592000, public")
                            .build(),
                    RequestBody.fromBytes(finalBytes)
            );

            return "https://" + bucket + ".s3.amazonaws.com/" + key;

        } catch (IOException e) {
            throw new RuntimeException("이미지 처리 또는 S3 업로드 실패", e);
        }
    }



    // PNG → JPEG 변환
    private byte[] convertPngToJpeg(BufferedImage image, float quality) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("JPEG ImageWriter를 찾을 수 없습니다.");
        }

        ImageWriter writer = writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();

        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);

        MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream(baos);
        writer.setOutput(output);

        writer.write(null, new IIOImage(image, null, null), param);
        writer.dispose();

        return baos.toByteArray();
    }



    // 단일 삭제
    public void delete(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);

            if (!key.isEmpty()) {
                s3Client.deleteObject(
                        DeleteObjectRequest.builder()
                                .bucket(bucket)
                                .key(key)
                                .build()
                );
            }

        } catch (Exception ignored) {}
    }


    // URL → key 추출
    private String extractKeyFromUrl(String fileUrl) {
        if (fileUrl == null) return "";
        String[] parts = fileUrl.split(bucket + ".s3.amazonaws.com/");
        return (parts.length > 1) ? parts[1] : "";
    }


    private String getExtension(String filename) {
        if (filename == null) return "dat";
        int idx = filename.lastIndexOf('.');
        if (idx == -1) return "dat";
        return filename.substring(idx + 1);
    }

    // 폴더 삭제
    public void deleteFolder(String prefix) {
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix)
                .build();

        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

        for (S3Object obj : listResponse.contents()) {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(obj.key())
                            .build()
            );
        }

        System.out.println("[S3 폴더 삭제 완료] prefix=" + prefix);
    }

}

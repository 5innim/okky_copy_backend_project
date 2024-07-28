package com.innim.okkycopy.global.common.storage;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode500Exception;
import com.innim.okkycopy.global.error.exception.StatusCodeException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.s3.folder-path}")
    private String folderPath;

    public String uploadFileToS3(MultipartFile multipartFile) throws IOException {
        File uploadFile = convert(multipartFile)
            .orElseThrow(() -> new StatusCodeException(ErrorCase._500_FILE_NOT_CREATED));

        try {
            String filePath = folderPath + "/" + UUID.randomUUID();
            return putFileToS3(uploadFile, filePath);
        } catch (Exception ex) {
            throw new StatusCode500Exception(ErrorCase._500_PUT_S3_FAIL);
        } finally {
            removeNewFile(uploadFile);
        }
    }

    public String putFileToS3(File uploadFile, String filePath) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, filePath, uploadFile).withCannedAcl(
            CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, filePath).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("file upload fail");
            return;
        }
        log.info("file upload success");
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        String filePath = System.getProperty("user.dir") + "/" + file.getOriginalFilename();
        File convertFile = new File(filePath);

        if (convertFile.createNewFile()) {
            FileOutputStream fos = new FileOutputStream(convertFile);
            fos.write(file.getBytes());

            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

}

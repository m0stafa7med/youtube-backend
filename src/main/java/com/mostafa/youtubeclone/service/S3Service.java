package com.mostafa.youtubeclone.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service implements FileService {

    @Value("${cloud.aws.bucket.name}")
    public String bucketName;
    private final AmazonS3Client amazonS3Client;

    @Override
    public String uploadFile(MultipartFile multipartFile) {

        //prepare key
        String filenameExtension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
        String key = UUID.randomUUID() + "." + filenameExtension;
        System.out.println(key);

        var metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try {
            amazonS3Client.putObject(bucketName, key, multipartFile.getInputStream(), metadata);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "an exception occured while uploading the file");
        }

        amazonS3Client.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
        return amazonS3Client.getResourceUrl(bucketName, key);
    }
}

package com.jjcsa.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.jjcsa.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Slf4j
@Service
public class AWSS3Service {

    @Value("${aws.s3.bucketname}")
    private String bucketName;

    @Value("${aws.region}")
    private String awsRegion;

    private AmazonS3 s3Client;

    private AmazonS3 getS3Client() {
        if(s3Client == null) {
            s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(awsRegion)
                    .build();
        }
        return s3Client;
    }

    public String saveFile(String objectKey, MultipartFile multipartFile) {
        File file = ImageUtil.convertMultiPartFileToFile(multipartFile);
        try {
            getS3Client().putObject(bucketName, objectKey, file);
        } catch (AmazonServiceException e) {
            log.error("Unable to save image to s3: " + e.getErrorMessage());
            return "";
        } finally {
            // delete the file created locally
            file.delete();
        }

        return getS3Client().getUrl(bucketName, objectKey).toString();
    }

    public void deleteFile(String objectKey) {
        try {
            getS3Client().deleteObject(bucketName, objectKey);
        } catch (AmazonServiceException e) {
            log.error("Unable to delete image from s3: " + e.getErrorMessage());
        }
    }
}

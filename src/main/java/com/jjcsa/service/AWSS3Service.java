package com.jjcsa.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.jjcsa.exception.UnknownServerErrorException;
import com.jjcsa.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class AWSS3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${aws.s3.bucketname}")
    private String bucketName;

    @Value("${spring.profiles.active:local}")
    private String activeProfiles;

    private void createBucket() {
        log.debug("Creating S3 bucker with name: {}", bucketName);
        amazonS3Client.createBucket(bucketName);
    }

    public String saveFile(String objectKey, MultipartFile multipartFile) {
        File file = ImageUtil.convertMultiPartFileToFile(multipartFile);
        try {
            if(activeProfiles.contains("local")) {
                return ImageUtil.saveFileLocally(file, objectKey);
            }

            if (!amazonS3Client.doesBucketExist(bucketName)) {
                createBucket();
            }
            log.debug("Saving object {} to S3", objectKey);
            amazonS3Client.putObject(bucketName, objectKey, file);
        } catch (AmazonServiceException e) {
            log.error("Unable to save image to s3: " + e.getErrorMessage());
            throw new UnknownServerErrorException("Error saving file",
                    "Error saving file to S3",
                    "Something went wrong with AWS S3 Service",
                    "Please try again after some time",
                    "");
        } finally {
            // delete the file created locally
            file.delete();
        }

        return amazonS3Client.getUrl(bucketName, objectKey).toString();
    }

    public void deleteFile(String objectKey) {
        try {

            if(activeProfiles.contains("local")) {
                return;
            }

            if (!amazonS3Client.doesBucketExist(bucketName)) {
                createBucket();
            }
            log.debug("Deleting object {} from S3", objectKey);
            amazonS3Client.deleteObject(bucketName, objectKey);
        } catch (AmazonServiceException e) {
            log.error("Unable to delete image from s3: " + e.getErrorMessage());
        }
    }
}

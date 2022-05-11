package com.jjcsa.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.DefaultRequest;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.jjcsa.exception.UnknownServerErrorException;
import com.jjcsa.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URL;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AWSS3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${aws.s3.bucketname}")
    private String bucketName;

    @Value("${spring.profiles.active:local}")
    private String activeProfiles;

    @Value("${aws.s3.region}")
    private String bucketRegion;

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

    public String generateSignedURLFromS3(String userId, String docType){
        URL url = null;
        String key = userId +"/"+docType;
        try {
            AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                    .withRegion(bucketRegion)
                    .withCredentials(new ProfileCredentialsProvider())
                    .build();

            // Set the presigned URL to expire after one hour.
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = Instant.now().toEpochMilli();
            expTimeMillis += 1000 * 60 * 60;
            expiration.setTime(expTimeMillis);

            // Generate the presigned URL.
            System.out.println("Generating pre-signed URL.");
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, key)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);
             url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

            System.out.println("Pre-Signed URL: " + url.toString());
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
        return url.toString();
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

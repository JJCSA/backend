package com.jjcsa.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.jjcsa.exception.UnknownServerErrorException;
import com.jjcsa.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AWSS3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${aws.s3.bucketname}")
    private String bucketName;

    @Value("${spring.profiles.active:local}")
    private String activeProfiles;

    @Value("${cloud.aws.s3.region:us-east-2}")
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

    public byte[] getImageFromS3(String userId, String fileType){
        try {
            byte[] content;

            String keyName = userId + File.separator + fileType;

            S3Object s3object = amazonS3Client.getObject(new GetObjectRequest(bucketName, keyName));

            final S3ObjectInputStream stream = s3object.getObjectContent();
            content = IOUtils.toByteArray(stream);
            s3object.close();
            return content;
        } catch (IOException ioException) {
            log.error("IOException: " + ioException.getMessage());
        } catch (AmazonServiceException serviceException) {
            log.error("AmazonServiceException Message:    " + serviceException.getMessage());
            throw serviceException;
        } catch (AmazonClientException clientException) {
            log.error("AmazonClientException Message: " + clientException.getMessage());
            throw clientException;
        }
        return null;
    }

    /**
     * Method to generate pre-signed url from S3.
     * @param documentURL url from the DB user. It will be combination of userId + profile picture.
     * @return generated pre-signed URL from S3
     */
    public String generateSignedURLFromS3(String documentURL) {

        if(activeProfiles.contains("local")) {
            return "localUrl";
        }

        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + 10 * 30 * 40);
        try{
            if (documentURL == null) {
                throw new IllegalArgumentException("documentURL must not be null!");
            }

            String extension = "";

            int index = documentURL.lastIndexOf('.');
            if (index > 0) {
                extension = documentURL.substring(index + 1);
            }
            ResponseHeaderOverrides responseHeaders = new ResponseHeaderOverrides();
            responseHeaders.setContentType("image/" +extension);
            // Generate the presigned URL.
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, documentURL)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);

            generatePresignedUrlRequest.setResponseHeaders(responseHeaders);

           return (amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest).toString());
        } catch (SdkClientException sdkClientException) {
            log.info("Error Occurred while getting pre-signed URL :{}, stack trace: {}" ,
                    sdkClientException.getMessage(), sdkClientException.getStackTrace());
        }
        return null;
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

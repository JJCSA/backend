package com.jjcsa.service;

import com.amazonaws.*;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.jjcsa.exception.UnknownServerErrorException;
import com.jjcsa.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    @Value("${cloud.aws.s3.region}")
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

    public String generateSignedURLFromS3(String userId, String s3Url){
        URL url = null;
        String documentName[] = s3Url.split("/");
        String key = userId + "/" + documentName[documentName.length - 1];
        try {
            // Set the presigned URL to expire after 10 sec.
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 10 * 30 * 40;
            expiration.setTime(expTimeMillis);

            // Generate the presigned URL.
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, key)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);
             url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

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

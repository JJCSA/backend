package com.jjcsa.service;

import com.jjcsa.exception.UnknownServerErrorException;
import com.jjcsa.model.User;
import com.jjcsa.repository.UserRespository;
import com.jjcsa.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRespository userRepository;
    @Autowired
    private AWSS3Service awss3Service;

    public User getUser(UUID id) {
        Optional<User> user = userRepository.findById(id);
        return null;
    }

    public User saveUser(User user, MultipartFile jainProofDoc, MultipartFile profPicture) {

        user = userRepository.save(user);

        if(user == null)
            throw new UnknownServerErrorException(
                    "Unable to store user profile",
                    "Error while store user profile in database",
                    "Please try again later",
                    "",
                    "");

        String jainProofDocURL = saveJainProofForUserProfile(user, jainProofDoc);
        if(jainProofDocURL == null) {
            throw new UnknownServerErrorException(
                    "Unable to save Jain Proof Doc to S3",
                    "Jain Proof Doc upload failed!",
                    "Please try again later",
                    "Please retry action later",
                    "");
        }

        String profPictureURL = saveProfilePictureForUserProfile(user, profPicture);
        if(profPictureURL == null) {
            throw new UnknownServerErrorException(
                    "Unable to save Profile Picture to S3",
                    "Profile Picture upload failed!",
                    "Please try again later",
                    "Please retry action later",
                    "");
        }

        user.setCommunityDocumentURL(jainProofDocURL);
        user.setProfilePictureURL(profPictureURL);
        return user;
    }

    public String saveJainProofForUserProfile(User user, MultipartFile jainProofDoc) {
        String jainProofDocURL = null;
        try {
            // Save MultipartFile to S3
            if(user.getCommunityDocumentURL() != null
                    && !user.getCommunityDocumentURL().isEmpty()) {
                // Delete file if already present
                deleteCommunityDocumentForUserProfile(user);
            }
            String fileKey = user.getId() + "/" + ImageUtil.generateCommunityDocumentName(jainProofDoc);
            jainProofDocURL = awss3Service.saveFile(fileKey, jainProofDoc);
            log.info("(S3 upload) Jain Proof Doc for user email {} uploaded", user.getEmail());
        } catch(Exception e) {
            log.error("Error while saving Jain Proof Doc to S3");
            log.error(e.getMessage());
        } finally {
            return jainProofDocURL;
        }
    }

    public String saveProfilePictureForUserProfile(User user, MultipartFile profPicture) {
        String profPictureURL = null;
        try {
            // Save MultipartFile to S3
            if(user.getProfilePictureURL() != null
                    && !user.getProfilePictureURL().isEmpty()) {
                // Delete file if already present
                deleteProfilePictureForUserProfile(user);
            }
            String fileKey = user.getId() + "/" + ImageUtil.generateProfilePictureName(profPicture);
            profPictureURL = awss3Service.saveFile(fileKey, profPicture);
            log.info("(S3 upload) Profile Picture for user email {} uploaded", user.getEmail());
        } catch(Exception e) {
            log.error("Error while saving Profile Picture to S3");
            log.error(e.getMessage());
        } finally {
            return profPictureURL;
        }
    }

    public void deleteProfilePictureForUserProfile(User user) {
        if(user.getProfilePictureURL() == null) return;
        awss3Service.deleteFile(ImageUtil.generateProfilePictureKeyForUserProfile(user));
        user.setProfilePictureURL(null);
    }

    public void deleteCommunityDocumentForUserProfile(User user) {
        if(user.getCommunityDocumentURL() == null) return;
        awss3Service.deleteFile(ImageUtil.generateCommunityDocumentKeyForUserProfile(user));
        user.setCommunityDocumentURL(null);
    }

    public void deleteUser(User user) {
        deleteProfilePictureForUserProfile(user);
        deleteCommunityDocumentForUserProfile(user);
        userRepository.delete(user);
    }

    public List<User> getallUsers()
    {
        List<User> users = null;

        try {
            users = (List<User>)userRepository.findAll();
        } catch (NullPointerException e) {
            log.error("There are no details available in database",e);
        } catch(Exception e){
            log.error("Something went wrong",e);
        }

        return users;
    }
}

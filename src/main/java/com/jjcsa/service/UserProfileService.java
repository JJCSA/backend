package com.jjcsa.service;

import com.jjcsa.exception.UnknownServerErrorException;
import com.jjcsa.model.UserProfile;
import com.jjcsa.repository.UserProfileRespository;
import com.jjcsa.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserProfileService {

    @Autowired
    private UserProfileRespository userProfileRepository;
    @Autowired
    private AWSS3Service awss3Service;

    public UserProfile getUserProfile(UUID id) {
        Optional<UserProfile> user = userProfileRepository.findById(id);
        return null;
    }

    public UserProfile saveUserProfile(UserProfile userProfile, MultipartFile jainProofDoc, MultipartFile profPicture) {

        String jainProofDocURL = saveJainProofForUserProfile(userProfile, jainProofDoc);
        if(jainProofDocURL == null) {
            throw new UnknownServerErrorException(
                    "Unable to save Jain Proof Doc to S3",
                    "Jain Proof Doc upload failed!",
                    "Please try again later",
                    "Please retry action later",
                    "");
        }

        String profPictureURL = saveProfilePictureForUserProfile(userProfile, profPicture);
        if(profPictureURL == null) {
            throw new UnknownServerErrorException(
                    "Unable to save Profile Picture to S3",
                    "Profile Picture upload failed!",
                    "Please try again later",
                    "Please retry action later",
                    "");
        }

        userProfile.setCommunityDocumentURL(jainProofDocURL);
        userProfile.setProfilePictureURL(profPictureURL);
        return userProfileRepository.save(userProfile);
    }

    public String saveJainProofForUserProfile(UserProfile userProfile, MultipartFile jainProofDoc) {
        String jainProofDocURL = null;
        try {
            // Save MultipartFile to S3
            if(userProfile.getCommunityDocumentURL() != null
                    && !userProfile.getCommunityDocumentURL().isEmpty()) {
                // Delete file if already present
                deleteCommunityDocumentForUserProfile(userProfile);
            }
            String fileKey = userProfile.getUserLogin().getId() + "/" + ImageUtil.generateCommunityDocumentName(jainProofDoc);
            jainProofDocURL = awss3Service.saveFile(fileKey, jainProofDoc);
            log.info("(S3 upload) Jain Proof Doc for user email {} uploaded", userProfile.getUserLogin().getEmail());
        } catch(Exception e) {
            log.error("Error while saving Jain Proof Doc to S3");
            log.error(e.getMessage());
        } finally {
            return jainProofDocURL;
        }
    }

    public String saveProfilePictureForUserProfile(UserProfile userProfile, MultipartFile profPicture) {
        String profPictureURL = null;
        try {
            // Save MultipartFile to S3
            if(userProfile.getProfilePictureURL() != null
                    && !userProfile.getProfilePictureURL().isEmpty()) {
                // Delete file if already present
                deleteProfilePictureForUserProfile(userProfile);
            }
            String fileKey = userProfile.getUserLogin().getId() + "/" + ImageUtil.generateProfilePictureName(profPicture);
            profPictureURL = awss3Service.saveFile(fileKey, profPicture);
            log.info("(S3 upload) Profile Picture for user email {} uploaded", userProfile.getUserLogin().getEmail());
        } catch(Exception e) {
            log.error("Error while saving Profile Picture to S3");
            log.error(e.getMessage());
        } finally {
            return profPictureURL;
        }
    }

    public void deleteProfilePictureForUserProfile(UserProfile userProfile) {
        if(userProfile.getProfilePictureURL() == null) return;
        awss3Service.deleteFile(ImageUtil.generateProfilePictureKeyForUserProfile(userProfile));
        userProfile.setProfilePictureURL(null);
    }

    public void deleteCommunityDocumentForUserProfile(UserProfile userProfile) {
        if(userProfile.getCommunityDocumentURL() == null) return;
        awss3Service.deleteFile(ImageUtil.generateCommunityDocumentKeyForUserProfile(userProfile));
        userProfile.setCommunityDocumentURL(null);
    }

    public void deleteUserProfile(UserProfile userProfile) {
        deleteProfilePictureForUserProfile(userProfile);
        deleteCommunityDocumentForUserProfile(userProfile);
        userProfileRepository.delete(userProfile);
    }
}

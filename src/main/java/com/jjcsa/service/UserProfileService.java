package com.jjcsa.service;

import com.jjcsa.model.UserProfile;
import com.jjcsa.repository.UserProfileRespository;
import com.jjcsa.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRespository userProfileRepository;
    @Autowired
    private AWSS3Service awss3Service;

    public UserProfile getUserProfile(UUID id) {
        Optional<UserProfile> user = userProfileRepository.findById(id);
        return null;
    }

    public UserProfile saveUserProfile(UserProfile userProfile, MultipartFile jainProofDoc) {
        // Save MultipartFile to S3
        if(userProfile.getCommunityDocumentURL() != null
            && !userProfile.getCommunityDocumentURL().isEmpty()) {
            // Delete file if already present
            String key = userProfile.getId() + "/COMMUNITY_DOCUMENT."
                    + userProfile.getCommunityDocumentURL().substring(userProfile.getCommunityDocumentURL().lastIndexOf(".")+1);
            awss3Service.deleteFile(key);
        }
        String fileKey = userProfile.getUserLogin().getId() + "/" + ImageUtil.generateCommunityDocumentName(jainProofDoc);
        String jainProofDocURL = awss3Service.saveFile(fileKey, jainProofDoc);
        userProfile.setCommunityDocumentURL(jainProofDocURL);

        return userProfileRepository.save(userProfile);
    }

    public void deleteUserProfile(UserProfile userProfile) {
        userProfileRepository.delete(userProfile);
    }
}

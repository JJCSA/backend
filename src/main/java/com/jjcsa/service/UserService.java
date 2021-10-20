package com.jjcsa.service;

import com.jjcsa.exception.BadRequestException;
import com.jjcsa.exception.UnknownServerErrorException;
import com.jjcsa.model.AdminAction;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.Action;
import com.jjcsa.model.enumModel.UserStatus;
import com.jjcsa.repository.AdminActionRepository;
import com.jjcsa.repository.UserRepository;
import com.jjcsa.util.ImageUtil;
import com.jjcsa.util.KeycloakUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class UserService {

    private UserRepository userRepository;
    private AWSS3Service awss3Service;
    private AdminActionRepository adminActionRepository;

    public User getUser(String email) {
        return userRepository.findUserByEmail(email);
    }

    public User getUserById(UUID userId) {
        return userRepository.findUserById(userId);
    }

    /**
     * Need to come up with idea to execute this method as a transaction.
     *
     * @param user
     * @param jainProofDoc
     * @param profPicture
     * @return
     */
    public User saveUser(User user, MultipartFile jainProofDoc, MultipartFile profPicture) {

        log.info("Save User Invoked for User:{}", user);
        if (nonNull(getUser(user.getEmail())))
            throw new BadRequestException(
                    "User already exists",
                    "User with this email address already exists",
                    "Please try logging in",
                    "",
                    ""
            );

        // Set defaults
        user.setUserStatus(UserStatus.Pending);

        user = userRepository.save(user);

        if (user == null)
            throw new UnknownServerErrorException(
                    "Unable to store user profile",
                    "Error while store user profile in database",
                    "Please try again later",
                    "",
                    "");

        String jainProofDocURL = saveJainProofForUserProfile(user, jainProofDoc);
        if (jainProofDocURL == null) {
            throw new UnknownServerErrorException(
                    "Unable to save Jain Proof Doc to S3",
                    "Jain Proof Doc upload failed!",
                    "Please try again later",
                    "Please retry action later",
                    "");
        }

        String profPictureURL = saveProfilePictureForUserProfile(user, profPicture);
        if (profPictureURL == null) {
            throw new UnknownServerErrorException(
                    "Unable to save Profile Picture to S3",
                    "Profile Picture upload failed!",
                    "Please try again later",
                    "Please retry action later",
                    "");
        }

        user.setCommunityDocumentURL(jainProofDocURL);
        user.setProfilePicture(profPictureURL);
        return user;
    }

    public String saveJainProofForUserProfile(User user, MultipartFile jainProofDoc) {
        String jainProofDocURL = null;
        try {
            // Save MultipartFile to S3
            if (user.getCommunityDocumentURL() != null
                    && !user.getCommunityDocumentURL().isEmpty()) {
                // Delete file if already present
                deleteCommunityDocumentForUserProfile(user);
            }
            String fileKey = user.getId() + "/" + ImageUtil.generateCommunityDocumentName(jainProofDoc);
            jainProofDocURL = awss3Service.saveFile(fileKey, jainProofDoc);
            log.info("(S3 upload) Jain Proof Doc for user email {} uploaded", user.getEmail());
        } catch (Exception e) {
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
            if (user.getProfilePicture() != null
                    && !user.getProfilePicture().isEmpty()) {
                // Delete file if already present
                deleteProfilePictureForUserProfile(user);
            }
            String fileKey = user.getId() + "/" + ImageUtil.generateProfilePictureName(profPicture);
            profPictureURL = awss3Service.saveFile(fileKey, profPicture);
            log.info("(S3 upload) Profile Picture for user email {} uploaded", user.getEmail());
        } catch (Exception e) {
            log.error("Error while saving Profile Picture to S3");
            log.error(e.getMessage());
        } finally {
            return profPictureURL;
        }
    }

    public void deleteProfilePictureForUserProfile(User user) {
        if (user.getProfilePicture() == null) return;
        awss3Service.deleteFile(ImageUtil.generateProfilePictureKeyForUserProfile(user));
        user.setProfilePicture(null);
    }

    public void deleteCommunityDocumentForUserProfile(User user) {
        if (user.getCommunityDocumentURL() == null) return;
        awss3Service.deleteFile(ImageUtil.generateCommunityDocumentKeyForUserProfile(user));
        user.setCommunityDocumentURL(null);
    }

    public void deleteUser(User user) {

        log.info("Deleting user with email {}", user.getEmail());

        KeycloakUtil.deleteUser(user);
        this.deleteProfilePictureForUserProfile(user);
        this.deleteCommunityDocumentForUserProfile(user);
        userRepository.delete(user);
    }

    public void deleteUserInDbOnly(User user) {
        this.deleteProfilePictureForUserProfile(user);
        this.deleteCommunityDocumentForUserProfile(user);
        userRepository.delete(user);
    }

    public List<User> getAllUsers() {
        // TODO: Fetch Role from keycloak for User
        return userRepository.findAll();
    }

    /*
     * Updates user's status with the given status
     * returns true if successful
     */
    public boolean updateUserStatus(User user, UserStatus status, AdminAction adminAction) {

        UserStatus currentStatus = user.getUserStatus();

        switch (status) {
            case NewUser:
                if (currentStatus.equals(UserStatus.Active)
                        || currentStatus.equals(UserStatus.Rejected)) {
                    throw new BadRequestException("Cannot update UserStatus",
                            "Cannot update UserStatus for Active or Rejected Users",
                            "",
                            "",
                            "");
                }

                // Enable user in keycloak
                KeycloakUtil.enableUser(user.getEmail());

                adminAction.setAction(Action.APPROVE_USER);
                adminAction.setDescrip(String.format("User with email %s approved by Admin", user.getEmail()));
                break;
            case Active:
                if (currentStatus.equals(UserStatus.Pending)
                        || currentStatus.equals(UserStatus.Rejected)) {
                    throw new BadRequestException("Cannot update UserStatus",
                            "Cannot update UserStatus to Active for Pending or Rejected users",
                            "",
                            "",
                            "");
                }
                if (!hasUserCompletedOnboardingProfile(user)) {
                    throw new BadRequestException("Cannot update UserStatus to Active",
                            "User has not completed on-boarding profile",
                            "",
                            "",
                            "");
                }
                break;
            case Rejected:
                if (currentStatus.equals(UserStatus.NewUser)
                        || currentStatus.equals(UserStatus.Active)) {
                    throw new BadRequestException("Cannot update UserStatus",
                            "Cannot update UserStatus to Rejected for NewUser or Active users",
                            "",
                            "",
                            "");
                }
                log.info("Rejecting user with email {}", user.getEmail());
                adminAction.setAction(Action.REJECT_USER);
                adminAction.setDescrip(String.format("User with email %s rejected by Admin", user.getEmail()));

                this.deleteUser(user);
                adminActionRepository.save(adminAction);
                return true;
        }

        log.info("Updating user's status from {} to {}", currentStatus, status);

        user.setUserStatus(status);
        userRepository.save(user);
        adminActionRepository.save(adminAction);

        return true;
    }

    /*
     * Checks if User finished on-boarding profile
     * TODO: Add fields to check for on-boarding
     */
    private boolean hasUserCompletedOnboardingProfile(User user) {
        return true;
    }

}

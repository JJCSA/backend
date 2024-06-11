package com.jjcsa.service;

import com.jjcsa.dto.AddNewUser;
import com.jjcsa.dto.UpdateUserStatusDto;
import com.jjcsa.dto.UserDTO;
import com.jjcsa.mapper.UserMapper;
import com.jjcsa.model.AdminAction;
import com.jjcsa.model.Education;
import com.jjcsa.model.User;
import com.jjcsa.model.WorkEx;
import com.jjcsa.model.enumModel.Action;
import com.jjcsa.model.enumModel.EmailEvent;
import com.jjcsa.model.enumModel.UserStatus;
import com.jjcsa.repository.AdminActionRepository;
import com.jjcsa.repository.UserRepository;
import com.jjcsa.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final AWSS3Service awss3Service;
    private final AdminActionRepository adminActionRepository;
    private final KeycloakService keycloakService;
    private final UserMapper userMapper;
    private final EmailSenderService emailSenderService;
    private final NewEmailService newEmailService;

    @Value("${show-community-proof:false}")
    private boolean showCommunityProof;

    public User getUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    /**
     * Need to come up with idea to execute this method as a transaction.
     *
     * @param newUser
     * @param jainProofDoc
     * @param profPicture
     * @return
     */
    public User saveUser(AddNewUser newUser, MultipartFile jainProofDoc, MultipartFile profPicture) {

        log.info("Saving user for {}", newUser.getEmail());
        String userId = keycloakService.createNewUser(newUser);

        if(isNull(userId)) {
            log.error("Keycloak not able to create user with email {}", newUser.getEmail());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to create user in Keycloak");
        }

        User user = userMapper.toUserProfile(newUser);

        // Set defaults
        user.setId(userId);
        user.setUserStatus(UserStatus.Pending);

        user = userRepository.save(user);

        if (user == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while store user profile in database");

        if(showCommunityProof){
            String jainProofDocURL = saveJainProofForUserProfile(user, jainProofDoc);
            if (jainProofDocURL == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Jain Proof Doc upload failed!");
            }
            user.setCommunityDocumentURL(jainProofDocURL);
        }


        String profPictureURL = saveProfilePictureForUserProfile(user, profPicture);
        if (profPictureURL == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Profile Picture upload failed!");
        }

        user.setProfilePicture(profPictureURL);
        return user;
    }

    public String saveJainProofForUserProfile(User user, MultipartFile jainProofDoc) {
        String jainProofDocURL = null;
        String fileKey = null;
        try {
            // Save MultipartFile to S3
            if (user.getCommunityDocumentURL() != null
                    && !user.getCommunityDocumentURL().isEmpty()) {
                // Delete file if already present
                deleteCommunityDocumentForUserProfile(user);
            }
            //Store the key in User DB
            fileKey = user.getId() + File.separator + ImageUtil.generateCommunityDocumentName(jainProofDoc);
            jainProofDocURL = awss3Service.saveFile(fileKey, jainProofDoc);
            log.info("(S3 upload) Jain Proof Doc for user email {} uploaded", user.getEmail());
        } catch (Exception e) {
            log.error("Error while saving Jain Proof Doc to S3");
            log.error(e.getMessage());
        } finally {
            return fileKey;
        }
    }

    public String saveProfilePictureForUserProfile(User user, MultipartFile profPicture) {
        String profPictureURL = null;
        String fileKey = null;
        try {
            // Save MultipartFile to S3
            if (user.getProfilePicture() != null
                    && !user.getProfilePicture().isEmpty()) {
                // Delete file if already present
                deleteProfilePictureForUserProfile(user);
            }
            fileKey = user.getId() + File.separator + ImageUtil.generateProfilePictureName(profPicture);
            profPictureURL = awss3Service.saveFile(fileKey, profPicture);
            log.info("(S3 upload) Profile Picture for user email {} uploaded", user.getEmail());
        } catch (Exception e) {
            log.error("Error while saving Profile Picture to S3");
            log.error(e.getMessage());
        } finally {
            return fileKey;
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

        keycloakService.deleteUser(user);
        this.deleteProfilePictureForUserProfile(user);
        this.deleteCommunityDocumentForUserProfile(user);
        userRepository.delete(user);
    }

    public List<UserDTO> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
    }

    private UserDTO toUserDTO(User user) {
        UserDTO userDTO = userMapper.toUserDTO(user);
        userDTO.setUserRole(keycloakService.getUserRole(user.getId()));
        userDTO.setProfilePicture(awss3Service.generateSignedURLFromS3(user.getProfilePicture()));
        return userDTO;
    }
    /*
     * Updates user's status with the given status
     * returns true if successful
     */
    public boolean updateUserStatus(User user, User adminUser, UpdateUserStatusDto userStatusDto) {

        AdminAction adminAction = new AdminAction();
        adminAction.setFromUserId(adminUser.getId());
        adminAction.setToUserId(user.getId());
        adminAction.setDateOfAction(new Date());

        UserStatus currentStatus = user.getUserStatus();

        switch (userStatusDto.getStatus()) {
            case NewUser:
                if (currentStatus.equals(UserStatus.Active)
                        || currentStatus.equals(UserStatus.Rejected)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update UserStatus for Active or Rejected Users");
                }

                // Enable user in keycloak
                keycloakService.enableUser(user.getId());

                adminAction.setAction(Action.APPROVE_USER);
                adminAction.setDescrip(String.format("User with email %s approved by Admin", user.getEmail()));
                newEmailService.sendEmail(EmailEvent.APPROVED, user, "");

                user.setApprovedDate(new Date());
                userRepository.save(user);
                break;
            case Active:
                if (currentStatus.equals(UserStatus.Pending)
                        || currentStatus.equals(UserStatus.Rejected)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update UserStatus to Active for Pending or Rejected users");
                }
                if (!hasUserCompletedOnboardingProfile(user)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update UserStatus to Active - User has not completed on-boarding profile");
                }
                break;
            case Rejected:
                if (currentStatus.equals(UserStatus.NewUser)
                        || currentStatus.equals(UserStatus.Active)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update UserStatus to Rejected for NewUser or Active users");
                }
                String message = String.format("User with email %s rejected by admin with reason: %s",
                        user.getEmail(), userStatusDto.getRejectReason());
                log.info(message);
                adminAction.setAction(Action.REJECT_USER);
                adminAction.setDescrip(message);

                deleteUser(user);
                adminActionRepository.save(adminAction);

                newEmailService.sendEmail(EmailEvent.REJECTED, user, userStatusDto.getRejectReason());
                return true;
        }

        log.info("Updating user's status from {} to {}", currentStatus, userStatusDto.getStatus());

        user.setUserStatus(userStatusDto.getStatus());
        userRepository.save(user);
        adminActionRepository.save(adminAction);

        return true;
    }

    /*
     * Updates user's is_regional_contact column
     * returns true if successful
     */
    public boolean updateUserRegionalContact(User user, Boolean isRegionalContact, AdminAction adminAction) {

        if (isRegionalContact) {
            adminAction.setAction(Action.SET_USER_AS_REGIONAL_CONTACT);
            adminAction.setDescrip(
                    String.format("User with email %s set as Regional Contact by Admin %s",
                            user.getEmail(), adminAction.getFromUserId()));
        } else {
            adminAction.setAction(Action.UNSET_USER_AS_REGIONAL_CONTACT);
            adminAction.setDescrip(
                    String.format("User with email %s removed as Regional Contact by Admin %s",
                            user.getEmail(), adminAction.getFromUserId()));
        }

        log.info("Updating user's is_regional_contact from {} to {}", user.getIsRegionalContact(), isRegionalContact);

        user.setIsRegionalContact(isRegionalContact);
        userRepository.save(user);
        adminActionRepository.save(adminAction);

        return true;
    }

    /*
     * Checks if User finished on-boarding profile
     */
    public boolean hasUserCompletedOnboardingProfile(User user) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date defaultDate = new Date();
        try {
            defaultDate = simpleDateFormat.parse("1900-01-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (StringUtils.isEmpty(user.getFirstName())
                || StringUtils.isEmpty(user.getLastName())
                || StringUtils.isEmpty(user.getStreet())
                || StringUtils.isEmpty(user.getState())
                || StringUtils.isEmpty(user.getCity())
                || StringUtils.isEmpty(user.getZip())
                || StringUtils.isEmpty(user.getEmail())
                || StringUtils.isEmpty(user.getLinkedinUrl())
                || StringUtils.isEmpty(user.getDateOfBirth())
        ) {
            return false;
        }

        // validate education
        List<Education> educationList = user.getEducationList();
        if (CollectionUtils.isEmpty(educationList)) return false;
        Education education = educationList.get(0);
        if (StringUtils.isEmpty(education.getUniversityName())
            || StringUtils.isEmpty(education.getDegree())
        ) {
            return false;
        }

        // validate work exp
        if (user.getUserStudent() == null
            || (user.getUserStudent() != null && !user.getUserStudent())) {
            List<WorkEx> workExperiences = user.getWorkExperience();
            if (CollectionUtils.isEmpty(workExperiences)) return false;

            // validate each work exp
            for (WorkEx workEx : workExperiences) {
                if (StringUtils.isEmpty(workEx.getCompanyName())
                    || StringUtils.isEmpty(workEx.getRole())
                    || StringUtils.isEmpty(workEx.getLocation())
                ) {
                    return false;
                }
            }
        }

        return true;
    }

    public String getCommunityProof(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if(isNull(user)) {
            log.error("Unable to find user with userId {}", userId);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to find user");
        }
        String communityProofUrl = null;
        communityProofUrl = awss3Service.generateSignedURLFromS3(user.getCommunityDocumentURL());
        if(isNull(communityProofUrl)){
            log.error("Unable to fetch community proof for userId {}", userId);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to fetch community proof");
        }
        log.info("community proof" + communityProofUrl);
        return communityProofUrl;
    }
}

package com.jjcsa.service;

import com.jjcsa.dto.AddNewUser;
import com.jjcsa.dto.UserDTO;
import com.jjcsa.exception.BadRequestException;
import com.jjcsa.exception.UnknownServerErrorException;
import com.jjcsa.mapper.UserMapper;
import com.jjcsa.model.AdminAction;
import com.jjcsa.model.Education;
import com.jjcsa.model.User;
import com.jjcsa.model.WorkEx;
import com.jjcsa.model.enumModel.Action;
import com.jjcsa.model.enumModel.Event;
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

    @Value("${show-community-proof:false}")
    private boolean showCommunityProof;

    public User getUser(String email) {
        return userRepository.findUserByEmail(email);
    }

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
            throw new UnknownServerErrorException(
                    "Unable to store user profile",
                    "Error while store user profile in database",
                    "Please try again later",
                    "",
                    "");

        if(showCommunityProof){
            String jainProofDocURL = saveJainProofForUserProfile(user, jainProofDoc);
            if (jainProofDocURL == null) {
                throw new UnknownServerErrorException(
                        "Unable to save Jain Proof Doc to S3",
                        "Jain Proof Doc upload failed!",
                        "Please try again later",
                        "Please retry action later",
                        "");
            }
            user.setCommunityDocumentURL(jainProofDocURL);
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

    public void deleteUserInDbOnly(User user) {
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
                keycloakService.enableUser(user.getId());

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

                deleteUser(user);
                adminActionRepository.save(adminAction);

                emailSenderService.sendEmail(user, Event.REJECTED);
                return true;
        }

        log.info("Updating user's status from {} to {}", currentStatus, status);

        user.setUserStatus(status);
        userRepository.save(user);
        adminActionRepository.save(adminAction);

        Event event = Event.resolveEmailEventUsingAdminAction(adminAction.getAction());

        if(event != null) {
            log.info("Sending Email for Event: {} for user:{}", event, user.getFirstName());
            // Send an email notification for Approved User
            emailSenderService.sendEmail(user, event);
        } else {
            log.info("Can not resolve email event for admin action :{}", adminAction.getAction());
        }

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
                || defaultDate.compareTo(user.getDateOfBirth()) >= 0 // compare user's dob with default date
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
        if (!user.isUserStudent()) {
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

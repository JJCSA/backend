package com.jjcsa.service;

import com.jjcsa.dto.AddNewUser;
import com.jjcsa.mapper.UserMapper;
import com.jjcsa.model.Education;
import com.jjcsa.model.User;
import com.jjcsa.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@Profile("test")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private AWSS3Service awss3Service;
    @Mock private KeycloakService keycloakService;
    @Mock private UserMapper userMapper;

    @Spy @InjectMocks private UserService userService;

    private User generateSampleUser() {
        return new User().builder()
                .id(UUID.randomUUID().toString())
                .email("test@test.com")
                .build();
    }

    private AddNewUser generateSampleUserDTO() {
        return AddNewUser.builder()
                .email("test@test.com")
                .build();
    }

    private MultipartFile generateSampleJainProofDoc() {
        try {
            File file = File.createTempFile("jainproof", "jpg");
            return new MockMultipartFile("jainproofdoc", file.getName(), "image/jpeg", new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private MultipartFile generateSampleProfPic() {
        try {
            File file = File.createTempFile("profpic", "jpg");
            return new MockMultipartFile("profpic", file.getName(), "image/jpeg", new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //@Test
    public void shouldSaveValidUser() {

        User sampleUser = generateSampleUser();
        MultipartFile jainProofDoc = generateSampleJainProofDoc();
        MultipartFile profPicture = generateSampleProfPic();

        when(keycloakService.createNewUser(any())).thenReturn(sampleUser.getId());
        when(userRepository.save(any())).thenReturn(sampleUser);
        when(userMapper.toUserProfile(any())).thenReturn(sampleUser);
        doReturn("jainproof.jpg").when(userService).saveJainProofForUserProfile(any(), any());
        doReturn("profpic.jpg").when(userService).saveProfilePictureForUserProfile(any(), any());

        User response = userService.saveUser(generateSampleUserDTO(), jainProofDoc, profPicture);
        assertNotNull(response);
        assertEquals(response.getId(), sampleUser.getId());
        assertEquals(response.getEmail(), sampleUser.getEmail());
        assertEquals(response.getCommunityDocumentURL(), "jainproof.jpg");
        assertEquals(response.getProfilePicture(), "profpic.jpg");
    }

//    TODO: Move this test to keycloak service
//    @Test
//    public void shouldValidateErrorIfUserAlreadyExists() {
//        when(userRepository.findUserByEmail(any())).thenReturn(sampleUser);
//
//        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.saveUser(generateSampleUserDTO(), jainProofDoc, profPicture));
//        assertEquals(exception.getMessage(), "User already exists");
//    }

    @Test
    public void shouldValidateErrorInUserSave() {
        User sampleUser = generateSampleUser();
        MultipartFile jainProofDoc = generateSampleJainProofDoc();
        MultipartFile profPicture = generateSampleProfPic();

        when(keycloakService.createNewUser(any())).thenReturn(sampleUser.getId());
        when(userMapper.toUserProfile(any())).thenReturn(sampleUser);
        when(userRepository.save(any())).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.saveUser(generateSampleUserDTO(), jainProofDoc, profPicture));
        assertEquals(exception.getReason(), "Error while store user profile in database");
    }

    @Test
    public void shouldValidateErrorInUserSaveFromKeycloak() {
        User sampleUser = generateSampleUser();
        MultipartFile jainProofDoc = generateSampleJainProofDoc();
        MultipartFile profPicture = generateSampleProfPic();

        when(keycloakService.createNewUser(any())).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.saveUser(generateSampleUserDTO(), jainProofDoc, profPicture));
        assertEquals(exception.getReason(), "Unable to create user in Keycloak");
    }

   // @Test
    public void shouldValidateJainProofURLOnSaveUser() {
        User sampleUser = generateSampleUser();
        MultipartFile jainProofDoc = generateSampleJainProofDoc();
        MultipartFile profPicture = generateSampleProfPic();

        when(keycloakService.createNewUser(any())).thenReturn(sampleUser.getId());
        when(userRepository.save(any())).thenReturn(sampleUser);
        when(userMapper.toUserProfile(any())).thenReturn(sampleUser);
        doReturn(null).when(userService).saveJainProofForUserProfile(any(), any());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.saveUser(generateSampleUserDTO(), jainProofDoc, profPicture));
        assertEquals(exception.getReason(), "Jain Proof Doc upload failed!");
    }

   // @Test
    public void shouldValidateProfPictureURLOnSaveUser() {
        User sampleUser = generateSampleUser();
        MultipartFile jainProofDoc = generateSampleJainProofDoc();
        MultipartFile profPicture = generateSampleProfPic();

        when(keycloakService.createNewUser(any())).thenReturn(sampleUser.getId());
        when(userRepository.save(any())).thenReturn(sampleUser);
        when(userMapper.toUserProfile(any())).thenReturn(sampleUser);
        doReturn("jainproof.jpg").when(userService).saveJainProofForUserProfile(any(), any());
        doReturn(null).when(userService).saveProfilePictureForUserProfile(any(), any());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.saveUser(generateSampleUserDTO(), jainProofDoc, profPicture));
        assertEquals(exception.getReason(), "Profile Picture upload failed!");
    }

    @Test
    public void shouldReturnFalseOnUserCompleteness() {
        User sampleUser = generateSampleUser();
        boolean result = userService.hasUserCompletedOnboardingProfile(sampleUser);
        assertFalse(result);
    }

    @Test
    public void shouldReturnTrueOnUserCompleteness() {
        User sampleUser = generateSampleUser();
        sampleUser.setFirstName("Firstname");
        sampleUser.setLastName("Lastname");
        sampleUser.setStreet("Street Address");
        sampleUser.setState("State");
        sampleUser.setCity("City");
        sampleUser.setZip("123456");
        sampleUser.setEmail("blah@blah.com");
        sampleUser.setLinkedinUrl("xyz.com");
        sampleUser.setDateOfBirth(new Date());
        sampleUser.setUserStudent(true);

        Education education = new Education();
        education.setUniversityName("University");
        education.setDegree("Degree");
        sampleUser.setEducationList(new ArrayList(){{add(education);}});

        boolean result = userService.hasUserCompletedOnboardingProfile(sampleUser);
        assertTrue(result);
    }
}

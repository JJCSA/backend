package com.jjcsa.service;

import com.jjcsa.exception.BadRequestException;
import com.jjcsa.exception.UnknownServerErrorException;
import com.jjcsa.model.User;
import com.jjcsa.repository.UserRepository;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@Profile("test")
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private AWSS3Service awss3Service;
    @Mock private KeycloakService keycloakService;

    @Spy @InjectMocks private UserService userService;

    private User sampleUser;
    private MultipartFile jainProofDoc;
    private MultipartFile profPicture;

    @BeforeAll
    public void loadData() {
        sampleUser = generateSampleUser();
        jainProofDoc = generateSampleJainProofDoc();
        profPicture = generateSampleProfPic();
    }

    private User generateSampleUser() {
        return new User().builder()
                .id(UUID.randomUUID())
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

    @Test
    public void shouldSaveValidUser() {
        when(userRepository.save(any())).thenReturn(sampleUser);
        doReturn("jainproof.jpg").when(userService).saveJainProofForUserProfile(any(), any());
        doReturn("profpic.jpg").when(userService).saveProfilePictureForUserProfile(any(), any());

        User response = userService.saveUser(sampleUser, jainProofDoc, profPicture);
        assertNotNull(response);
        assertEquals(response.getId(), sampleUser.getId());
        assertEquals(response.getEmail(), sampleUser.getEmail());
        assertEquals(response.getCommunityDocumentURL(), "jainproof.jpg");
        assertEquals(response.getProfilePicture(), "profpic.jpg");
    }

    @Test
    public void shouldValidateErrorIfUserAlreadyExists() {
        when(userRepository.findUserByEmail(any())).thenReturn(sampleUser);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.saveUser(sampleUser, jainProofDoc, profPicture));
        assertEquals(exception.getMessage(), "User already exists");
    }

    @Test
    public void shouldValidateErrorInUserSave() {
        when(userRepository.save(any())).thenReturn(null);

        UnknownServerErrorException exception = assertThrows(UnknownServerErrorException.class, () -> userService.saveUser(sampleUser, jainProofDoc, profPicture));
        assertEquals(exception.getMessage(), "Unable to store user profile");
    }

    @Test
    public void shouldValidateJainProofURLOnSaveUser() {
        when(userRepository.save(any())).thenReturn(sampleUser);
        doReturn(null).when(userService).saveJainProofForUserProfile(any(), any());

        UnknownServerErrorException exception = assertThrows(UnknownServerErrorException.class, () -> userService.saveUser(sampleUser, jainProofDoc, profPicture));
        assertEquals(exception.getMessage(), "Unable to save Jain Proof Doc to S3");
    }

    @Test
    public void shouldValidateProfPictureURLOnSaveUser() {
        when(userRepository.save(any())).thenReturn(sampleUser);
        doReturn("jainproof.jpg").when(userService).saveJainProofForUserProfile(any(), any());
        doReturn(null).when(userService).saveProfilePictureForUserProfile(any(), any());

        UnknownServerErrorException exception = assertThrows(UnknownServerErrorException.class, () -> userService.saveUser(sampleUser, jainProofDoc, profPicture));
        assertEquals(exception.getMessage(), "Unable to save Profile Picture to S3");
    }
}

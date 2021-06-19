package com.jjcsa.service;

import com.jjcsa.dto.UserProfile;
import com.jjcsa.exception.BadRequestException;
import com.jjcsa.mapper.UserProfileMapper;
import com.jjcsa.model.Education;
import com.jjcsa.model.User;
import com.jjcsa.model.WorkEx;
import com.jjcsa.model.enumModel.UserStatus;
import com.jjcsa.repository.EducationRepository;
import com.jjcsa.repository.WorkExRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Profile("test")
@ExtendWith(MockitoExtension.class)
public class UserProfileServiceTest {

    @Mock private UserService userService;
    @Mock private EducationRepository educationRepository;
    @Mock private WorkExRepository workExRepository;
    @Mock private UserProfileMapper userProfileMapper;

    @Spy @InjectMocks private UserProfileService userProfileService;

    private User generateUserData() {
        return new User().builder()
                .email("test@test.com")
                .userStatus(UserStatus.Active)
                .build();
    }


    private List<Education> generateEducationData() {
        return Arrays.asList(
                new Education().builder()
                    .educationId(1)
                    .build()
        );
    }

    private List<WorkEx> generateWorkExData() {
        return Arrays.asList(
                new WorkEx().builder()
                    .expId(1)
                    .build()
        );
    }

    private UserProfile generateUserProfile() {
        return new UserProfile().builder()
                .email("test@test.com")
                .education(generateEducationData())
                .workExperience(generateWorkExData())
                .build();
    }

    @Test
    public void testGetUserProfileFromEmail() {
        when(userService.getUser(any())).thenReturn(generateUserData());
        when(educationRepository.findAllByUser(any())).thenReturn(generateEducationData());
        when(workExRepository.findAllByUser(any())).thenReturn(generateWorkExData());
        when(userProfileMapper.toUserProfile(any())).thenReturn(generateUserProfile());

        UserProfile response = userProfileService.getUserProfile("test@test.com");
        assertNotNull(response);
        assertEquals(response.getEmail(), "test@test.com");
        assertEquals(response.getEducation().size(), 1);
        assertEquals(response.getEducation().get(0).getEducationId(), 1);
        assertEquals(response.getWorkExperience().size(), 1);
        assertEquals(response.getWorkExperience().get(0).getExpId(), 1);
    }

    @Test
    public void testGetUserProfileFromInvalidEmail() {
        when(userService.getUser(any())).thenReturn(null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userProfileService.getUserProfile("xyz"));
        assertEquals(exception.getMessage(), "User Profile does not exist");
    }

    @Test
    public void testGetUserProfileForInactiveUser() {
        User inactiveUser = generateUserData();
        inactiveUser.setUserStatus(UserStatus.Pending);

        when(userService.getUser(any())).thenReturn(inactiveUser);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> userProfileService.getUserProfile("test@test.com"));
        assertEquals(exception.getStatus(), HttpStatus.FORBIDDEN);
        assertEquals(exception.getReason(), "User is not active");
    }
}

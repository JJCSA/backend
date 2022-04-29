package com.jjcsa.service;

import com.jjcsa.dto.UserProfile;
import com.jjcsa.exception.BadRequestException;
import com.jjcsa.mapper.UserProfileMapper;
import com.jjcsa.model.Education;
import com.jjcsa.model.User;
import com.jjcsa.model.WorkEx;
import com.jjcsa.model.enumModel.UserRole;
import com.jjcsa.model.enumModel.UserStatus;
import com.jjcsa.repository.EducationRepository;
import com.jjcsa.repository.WorkExRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
    @Mock private KeycloakService keycloakService;

    @Spy @InjectMocks private UserProfileService userProfileService;

    private User generateUserData() {
        return new User().builder()
                .id("1")
                .email("test@test.com")
                .userStatus(UserStatus.Active)
                .build();
    }


    private List<Education> generateEducationData() {
        return Arrays.asList(
                new Education().builder()
                    .educationId(UUID.randomUUID())
                    .build()
        );
    }

    private List<WorkEx> generateWorkExData() {
        return Arrays.asList(
                new WorkEx().builder()
                    .expId(UUID.randomUUID())
                    .build()
        );
    }

    private UserProfile generateUserProfile() {
        return new UserProfile().builder()
                .id("1")
                .email("test@test.com")
                .education(generateEducationData())
                .workExperience(generateWorkExData())
                .userRole(UserRole.USER)
                .build();
    }

    @Test
    public void testGetUserProfileFromEmail() {
        when(userService.getUserById(any())).thenReturn(generateUserData());
//        when(educationRepository.findAllByUser(any())).thenReturn(generateEducationData());
//        when(workExRepository.findAllByUser(any())).thenReturn(generateWorkExData());
        when(keycloakService.getUserRole(any())).thenReturn(UserRole.USER);
        UserProfile userProfile = generateUserProfile();
        when(userProfileMapper.toUserProfile(any(), any())).thenReturn(userProfile);

        UserProfile response = userProfileService.getUserProfile("1");
        assertNotNull(response);
        assertEquals(response.getId(), "1");
        assertEquals(response.getEmail(), "test@test.com");
        assertEquals(response.getUserRole(), UserRole.USER);
        assertEquals(response.getEducation().size(), 1);
        assertEquals(response.getEducation().get(0).getEducationId(), userProfile.getEducation().get(0).getEducationId());
        assertEquals(response.getWorkExperience().size(), 1);
        assertEquals(response.getWorkExperience().get(0).getExpId(), userProfile.getWorkExperience().get(0).getExpId());
    }

    @Test
    public void testGetUserProfileFromInvalidEmail() {
        when(userService.getUserById(any())).thenReturn(null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userProfileService.getUserProfile("xyz"));
        assertEquals(exception.getMessage(), "User Profile does not exist");
    }

    @Test
    public void testGetUserProfileForInactiveUser() {
        User inactiveUser = generateUserData();
        inactiveUser.setUserStatus(UserStatus.Pending);

        when(userService.getUserById(any())).thenReturn(inactiveUser);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> userProfileService.getUserProfile("1"));
        assertEquals(exception.getStatus(), HttpStatus.FORBIDDEN);
        assertEquals(exception.getReason(), "User is pending");
    }
}

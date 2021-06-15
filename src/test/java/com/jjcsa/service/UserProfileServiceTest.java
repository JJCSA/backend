package com.jjcsa.service;

import com.jjcsa.mapper.UserProfileMapper;
import com.jjcsa.model.User;
import com.jjcsa.repository.EducationRepository;
import com.jjcsa.repository.WorkExRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;

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
                .build();
    }

    @Test
    public void testGetUserProfileFromEmail() {

    }
}

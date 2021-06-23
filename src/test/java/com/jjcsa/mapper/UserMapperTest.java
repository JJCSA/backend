package com.jjcsa.mapper;

import com.jjcsa.dto.AddNewUser;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.ContactMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Profile("test")
@ExtendWith(MockitoExtension.class)
public class UserMapperTest {
    @Spy UserMapperImpl mapper;

    @Test
    public void shouldMapAddNewUserToUser() {
        AddNewUser addNewUser = new AddNewUser().builder()
                .firstName("fname")
                .lastName("lname")
                .email("test@test.com")
                .mobileNumber("1234567890")
                .prefMethodOfContact("Email")
                .jainCommunity("comm")
                .build();

        User response = mapper.toUserProfile(addNewUser);
        assertNotNull(response);
        assertEquals(response.getFirstName(), "fname");
        assertEquals(response.getLastName(), "lname");
        assertEquals(response.getEmail(), "test@test.com");
        assertEquals(response.getMobileNumber(), "1234567890");
        assertEquals(response.getContactMethod(), ContactMethod.Email);
        assertEquals(response.getCommunityName(), "comm");
    }
}

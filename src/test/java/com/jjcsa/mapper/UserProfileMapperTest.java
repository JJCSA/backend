package com.jjcsa.mapper;

import com.jjcsa.dto.UserProfile;
import com.jjcsa.model.Education;
import com.jjcsa.model.User;
import com.jjcsa.model.WorkEx;
import com.jjcsa.model.enumModel.ContactMethod;
import com.jjcsa.model.enumModel.UserRole;
import com.jjcsa.model.enumModel.UserStatus;
import com.jjcsa.model.enumModel.VolunteeringInterest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Profile("test")
@ExtendWith(MockitoExtension.class)
public class UserProfileMapperTest {
    @Mock UserProfileMapper userProfileMapper;

    @Spy @InjectMocks UserProfileMapperImpl mapper;

    private List<Education> generateEducationData() {
        return Arrays.asList(
                new Education().builder()
                    .educationId(1)
                    .universityName("XYZ")
                    .gradMonth(1)
                    .gradYear(2020)
                    .specialization("Comps")
                    .degree("MS")
                    .build()
        );
    }

    private List<WorkEx> generateWorkExData() {
        return Arrays.asList(
                new WorkEx().builder()
                    .expId(1)
                    .companyName("XYZ")
                    .role("SDE")
                    .location("NYC")
                    .totalExp("1 month")
                    .build()
        );
    }

    @Test
    public void shouldMapUserToUserProfile() {
        UUID uuid = UUID.randomUUID();
        Date date = new Date();

        User user = new User().builder()
                .id(uuid)
                .email("test@test.com")
                .firstName("fname")
                .middleName("mname")
                .lastName("lname")
                .mobileNumber("1234567890")
                .contactMethod(ContactMethod.Email)
                .communityName("comm")
                .userStatus(UserStatus.Active)
                .street("test street")
                .city("test city")
                .state("test state")
                .zip("123456")
                .dateOfBirth(date)
                .profilePicture("xyz.com/prof.jpg")
                .socialMediaPlatform("Facebook")
                .volunteeringInterest(VolunteeringInterest.Yes)
                .linkedinUrl("linked.com/xyz")
                .build();
        user.setEducationList(generateEducationData());
        user.setWorkExperience(generateWorkExData());

        UserProfile response = mapper.toUserProfile(user);
        assertNotNull(response);
        assertEquals(response.getId(), uuid);
        assertEquals(response.getEmail(), "test@test.com");
        assertEquals(response.getFirstName(), "fname");
        assertEquals(response.getMiddleName(), "mname");
        assertEquals(response.getLastName(), "lname");
        assertEquals(response.getMobileNumber(), "1234567890");
        assertEquals(response.getContactMethod(), ContactMethod.Email);
        assertEquals(response.getCommunityName(), "comm");
        assertEquals(response.getUserStatus(), UserStatus.Active);
        assertEquals(response.getStreet(), "test street");
        assertEquals(response.getCity(), "test city");
        assertEquals(response.getState(), "test state");
        assertEquals(response.getZip(), "123456");
        assertEquals(response.getDateOfBirth(), date);
        assertEquals(response.getProfilePicture(), "xyz.com/prof.jpg");
        assertEquals(response.getSocialMediaPlatform(), "Facebook");
        assertEquals(response.getVolunteeringInterest(), VolunteeringInterest.Yes);
        assertEquals(response.getLinkedinUrl(), "linked.com/xyz");
        assertEquals(response.getEducation().size(), 1);
        assertEquals(response.getWorkExperience().size(), 1);

        Education respEducation = response.getEducation().get(0);
        assertNotNull(respEducation);
        assertEquals(respEducation.getEducationId(), 1);
        assertEquals(respEducation.getUniversityName(), "XYZ");
        assertEquals(respEducation.getGradMonth(), 1);
        assertEquals(respEducation.getGradYear(), 2020);
        assertEquals(respEducation.getSpecialization(), "Comps");
        assertEquals(respEducation.getDegree(), "MS");

        WorkEx respWorkEx = response.getWorkExperience().get(0);
        assertNotNull(respWorkEx);
        assertEquals(respWorkEx.getExpId(), 1);
        assertEquals(respWorkEx.getCompanyName(), "XYZ");
        assertEquals(respWorkEx.getRole(), "SDE");
        assertEquals(respWorkEx.getLocation(), "NYC");
        assertEquals(respWorkEx.getTotalExp(), "1 month");
    }

    @Test
    public void shouldMapUserProfileToUser() {
        UUID uuid = UUID.randomUUID();
        Date date = new Date();

        UserProfile userProfile = new UserProfile().builder()
                .id(uuid)
                .email("test@test.com")
                .firstName("fname")
                .middleName("mname")
                .lastName("lname")
                .mobileNumber("1234567890")
                .contactMethod(ContactMethod.Email)
                .communityName("comm")
                .userStatus(UserStatus.Active)
                .street("test street")
                .city("test city")
                .state("test state")
                .zip("123456")
                .dateOfBirth(new Date())
                .profilePicture("xyz.com/prof.jpg")
                .socialMediaPlatform("Facebook")
                .volunteeringInterest(VolunteeringInterest.Yes)
                .linkedinUrl("linked.com/xyz")
                .build();
        userProfile.setEducation(generateEducationData());
        userProfile.setWorkExperience(generateWorkExData());

        User response = mapper.toUser(userProfile);
        assertNotNull(response);
        assertEquals(response.getId(), uuid);
        assertEquals(response.getEmail(), "test@test.com");
        assertEquals(response.getFirstName(), "fname");
        assertEquals(response.getMiddleName(), "mname");
        assertEquals(response.getLastName(), "lname");
        assertEquals(response.getMobileNumber(), "1234567890");
        assertEquals(response.getContactMethod(), ContactMethod.Email);
        assertEquals(response.getCommunityName(), "comm");
        assertEquals(response.getUserStatus(), UserStatus.Active);
        assertEquals(response.getStreet(), "test street");
        assertEquals(response.getCity(), "test city");
        assertEquals(response.getState(), "test state");
        assertEquals(response.getZip(), "123456");
        assertEquals(response.getDateOfBirth(), date);
        assertEquals(response.getProfilePicture(), "xyz.com/prof.jpg");
        assertEquals(response.getSocialMediaPlatform(), "Facebook");
        assertEquals(response.getVolunteeringInterest(), VolunteeringInterest.Yes);
        assertEquals(response.getLinkedinUrl(), "linked.com/xyz");
        assertEquals(response.getEducationList().size(), 1);
        assertEquals(response.getWorkExperience().size(), 1);

        Education respEducation = response.getEducationList().get(0);
        assertNotNull(respEducation);
        assertEquals(respEducation.getEducationId(), 1);
        assertEquals(respEducation.getUniversityName(), "XYZ");
        assertEquals(respEducation.getGradMonth(), 1);
        assertEquals(respEducation.getGradYear(), 2020);
        assertEquals(respEducation.getSpecialization(), "Comps");
        assertEquals(respEducation.getDegree(), "MS");

        WorkEx respWorkEx = response.getWorkExperience().get(0);
        assertNotNull(respWorkEx);
        assertEquals(respWorkEx.getExpId(), 1);
        assertEquals(respWorkEx.getCompanyName(), "XYZ");
        assertEquals(respWorkEx.getRole(), "SDE");
        assertEquals(respWorkEx.getLocation(), "NYC");
        assertEquals(respWorkEx.getTotalExp(), "1 month");
    }
}

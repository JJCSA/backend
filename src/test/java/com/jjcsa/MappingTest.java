//package com.jjcsa;
//
//import com.jjcsa.dto.UserProfile;
//import com.jjcsa.mapper.UserProfileMapper;
//import com.jjcsa.model.Education;
//import com.jjcsa.model.User;
//import com.jjcsa.model.WorkEx;
//import com.jjcsa.model.enumModel.ContactMethod;
//import com.jjcsa.model.enumModel.UserStatus;
//import com.jjcsa.model.enumModel.VolunteeringInterest;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.Arrays;
//import java.util.Date;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//public class MappingTest {
//
//    @Autowired
//    UserProfileMapper userProfileMapper;
//
//    @Test
//    public void testMapping() {
//        User user = new User().builder()
//                .email("test@test.com")
//                .firstName("fname")
//                .middleName("mname")
//                .lastName("lname")
//                .mobileNumber("1234567890")
//                .contactMethod(ContactMethod.Email)
//                .communityName("Jain Community")
//                .userStatus(UserStatus.Active)
//                .street("street add")
//                .city("city")
//                .state("state")
//                .zip("12345")
//                .dateOfBirth(new Date())
//                .profilePictureURL("test url")
//                .socialMediaPlatform("LinkedIn")
//                .volInterest(VolunteeringInterest.Yes)
//                .linkedinUrl("test url")
//                .build();
//
//        Education education = new Education().builder()
//                .universityName("NEU")
//                .gradMonth(12)
//                .gradYear(2020)
//                .specialization("Information Systems")
//                .degree("Masters")
//                .build();
//
//        WorkEx workExperience = new WorkEx().builder()
//                .companyName("Appen")
//                .role("SE")
//                .location("SF")
//                .totalExp("5 years")
//                .build();
//
//        UserProfile userProfile = userProfileMapper.from(user, Arrays.asList(education), Arrays.asList(workExperience));
//        assertEquals(userProfile.getFirstName(), user.getFirstName());
//    }
//}

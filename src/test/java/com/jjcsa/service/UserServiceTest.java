package com.jjcsa.service;

import com.jjcsa.dto.AddNewUser;
import com.jjcsa.model.User;
import com.jjcsa.repository.UserRepository;
import com.jjcsa.util.ImageUtil;
import com.jjcsa.util.KeycloakUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceTest {

    // Need this as a test functionality of delete user from keycloak
    @Value("${keycloak.auth-server-url:http://localhost:8080/auth}")
    private String keycloakServerUrl;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AWSS3Service awss3Service;

    @Test
    void injectedComponentsAreNotNull(){
        Assertions.assertNotNull(userService);
        Assertions.assertNotNull(userRepository);
        Assertions.assertNotNull(awss3Service);
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getUser() {
    }

    private File getFile(String path){
        return new File(path);
    }

    private String getCommunityURL() throws IOException {
        File file = this.getFile("D:\\Projects\\JJC\\backend\\src\\test\\data\\communityDoc.jpeg");
        MultipartFile multipartFile = this.getMultiPartFileFromFile(file);
        return ImageUtil.generateCommunityDocumentName(multipartFile);
    }

    private String getProfileURL() throws IOException {
        File file = this.getFile("D:\\Projects\\JJC\\backend\\src\\test\\data\\profileDo.jpeg");
        MultipartFile multipartFile = this.getMultiPartFileFromFile(file);
        return ImageUtil.generateProfilePictureName(multipartFile);
    }

    private MultipartFile getMultiPartFileFromFile(File file) throws IOException {
        FileInputStream input = new FileInputStream(file);
        MultipartFile imageMultiPartFile = new MockMultipartFile(file.getName(),  IOUtils.toByteArray(input));
        return imageMultiPartFile;
    }

    private User getTestUser(){
        User user = new User();
        user.setFirstName("Test");
        user.setCity("city");
        user.setEmail("abc@gmail.com");
        user.setPassword("testUser");
        return user;
    }

    User saveUser() throws IOException {
        User user = this.getTestUser();
        File communityFile = this.getFile("D:\\Projects\\JJC\\backend\\src\\test\\data\\communityDoc.jpeg");
        MultipartFile communityMultiPartFile = this.getMultiPartFileFromFile(communityFile);

        File profileFile = this.getFile("D:\\Projects\\JJC\\backend\\src\\test\\data\\profileDoc.jpeg");
        MultipartFile profileMultiPartFile = this.getMultiPartFileFromFile(profileFile);

        log.info("User {} with files:{},{}",user,communityMultiPartFile.getName(),profileMultiPartFile.getName());
        User saveUser = userService.saveUser(user,communityMultiPartFile,profileMultiPartFile);

        AddNewUser newUser = AddNewUser.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .jainCommunity(user.getCommunityName())
                .mobileNumber(user.getMobileNumber())
                .password(user.getPassword())
                .build();

        KeycloakUtil.createNewUser(newUser,keycloakServerUrl);

        log.info("User after save:{}", saveUser.toString());
        return saveUser;
    }

    @Test
    void saveJainProofForUserProfile() {
    }

    @Test
    void saveProfilePictureForUserProfile() {
    }

    @Test
    void deleteProfilePictureForUserProfile() {
    }

    @Test
    void deleteCommunityDocumentForUserProfile() {
    }

    @Test
    void deleteUser() throws IOException {
        //  Assuming user is already present in database
        User savedUser = this.saveUser();
        log.info("Saved User:{} in delete User",savedUser.toString());

        User dbUser = userRepository.findUserByEmail(savedUser.getEmail());

        // Making sure user is there in database.
        Assertions.assertNotNull(dbUser);

        // Making sure user service delete the user without any error.
        Assertions.assertDoesNotThrow(()->userService.deleteUser(savedUser));
        User dbUserAfterDelete = userRepository.findUserByEmail(savedUser.getEmail());

        // Making sure user is not present in the database.
        Assertions.assertNull(dbUserAfterDelete);
    }

    @Test
    void getallUsers() {
    }
}
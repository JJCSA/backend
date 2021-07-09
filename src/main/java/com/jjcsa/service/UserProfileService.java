package com.jjcsa.service;

import com.jjcsa.dto.UserProfile;
import com.jjcsa.exception.BadRequestException;
import com.jjcsa.mapper.UserProfileMapper;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.UserStatus;
import com.jjcsa.repository.EducationRepository;
import com.jjcsa.repository.WorkExRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@Slf4j
@AllArgsConstructor
public class UserProfileService {

    private final UserService userService;
    private final EducationRepository educationRepository;
    private final WorkExRepository workExRepository;
    private final UserProfileMapper userProfileMapper;

    public UserProfile getUserProfile(String email) {
        User user = userService.getUser(email);
        if(user == null)
            throw new BadRequestException(
                    "User Profile does not exist",
                    "User's profile has not been created",
                    "",
                    "",
                    ""
            );

        if(!UserStatus.Active.equals(user.getUserStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "User is not active"
            );
        }

        user.setEducationList(educationRepository.findAllByUser(user));
        user.setWorkExperience(workExRepository.findAllByUser(user));

        return userProfileMapper.toUserProfile(user);
    }
}

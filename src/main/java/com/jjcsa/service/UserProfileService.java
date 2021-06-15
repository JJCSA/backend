package com.jjcsa.service;

import com.jjcsa.dto.UserProfile;
import com.jjcsa.exception.BadRequestException;
import com.jjcsa.mapper.UserProfileMapper;
import com.jjcsa.model.Education;
import com.jjcsa.model.User;
import com.jjcsa.model.WorkEx;
import com.jjcsa.repository.EducationRepository;
import com.jjcsa.repository.UserRepository;
import com.jjcsa.repository.WorkExRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserProfileService {

    private final UserService userService;
    private final UserRepository userRepository;
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

//        user.setEducationList(educationRepository.findAllByUser(user));
//        user.setWorkExperience(workExRepository.findAllByUser(user));
        List<Education> educationList = educationRepository.findAllByUser(user);
        List<WorkEx> workExperiences = workExRepository.findAllByUser(user);

        UserProfile userProfile = userProfileMapper.convert(user);
        userProfileMapper.addEducationAndWorkExperience(userProfile, educationList, workExperiences);

        return userProfile;

    }
}

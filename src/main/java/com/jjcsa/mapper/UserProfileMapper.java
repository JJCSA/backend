package com.jjcsa.mapper;

import com.jjcsa.dto.UserProfile;
import com.jjcsa.model.Education;
import com.jjcsa.model.User;
import com.jjcsa.model.WorkEx;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    UserProfile convert(User user);

    default UserProfile addEducationAndWorkExperience(UserProfile userProfile, List<Education> educationList, List<WorkEx> workExperiences) {
        if(educationList == null || workExperiences == null) {
            return null;
        }

        userProfile.setEducation(educationList);
        userProfile.setWorkExperience(workExperiences);

        return userProfile;
    }
}

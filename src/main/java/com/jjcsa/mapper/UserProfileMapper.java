package com.jjcsa.mapper;

import com.jjcsa.dto.UserProfile;
import com.jjcsa.model.Education;
import com.jjcsa.model.User;
import com.jjcsa.model.WorkEx;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mappings({
            @Mapping(source = "user.educationList", target = "education")
    })
    UserProfile toUserProfile(User user);

    @Mappings({
            @Mapping(source = "userProfile.education", target = "educationList")
    })
    User toUser(UserProfile userProfile);

}

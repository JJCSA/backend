package com.jjcsa.mapper;

import com.jjcsa.dto.UserProfile;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mappings({
            @Mapping(source = "user.educationList", target = "education"),
            @Mapping(source = "userRole", target = "userRole"),
            @Mapping(source = "user.userStudent", target = "userStudent"),
            @Mapping(source = "profileS3Url", target = "profilePicture")
    })
    UserProfile toUserProfile(User user, UserRole userRole, String profileS3Url);

    @Mappings({
            @Mapping(source = "userProfile.education", target = "educationList"),
            @Mapping(source = "userProfile.userStudent", target = "userStudent")
    })
    User toUser(UserProfile userProfile);

}

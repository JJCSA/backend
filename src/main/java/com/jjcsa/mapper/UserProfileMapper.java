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
            @Mapping(source = "userRole", target = "userRole")
    })
    UserProfile toUserProfile(User user, UserRole userRole);

    @Mappings({
            @Mapping(source = "userProfile.education", target = "educationList")
    })
    User toUser(UserProfile userProfile);

}

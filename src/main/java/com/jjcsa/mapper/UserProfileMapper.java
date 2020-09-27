package com.jjcsa.mapper;

import com.jjcsa.dto.AddNewUser;
import com.jjcsa.model.UserProfile;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfile toUserProfile(AddNewUser addNewUser);
}

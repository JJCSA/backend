package com.jjcsa.mapper;

import com.jjcsa.dto.AddNewUser;
import com.jjcsa.model.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserProfileMapper {

    @Mappings({
            @Mapping(source = "addNewUser.prefMethodOfContact", target = "contactMethod")
    })
    UserProfile toUserProfile(AddNewUser addNewUser);
}

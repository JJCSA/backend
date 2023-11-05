package com.jjcsa.mapper;

import com.jjcsa.dto.AddNewUser;
import com.jjcsa.dto.UserDTO;
import com.jjcsa.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(source = "addNewUser.prefMethodOfContact", target = "contactMethod"),
            @Mapping(source = "addNewUser.jainCommunity", target = "communityName")
    })
    User toUserProfile(AddNewUser addNewUser);

    UserDTO toUserDTO(User user);
}

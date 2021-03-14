package com.jjcsa.mapper;

import com.jjcsa.dto.AddNewUser;
import com.jjcsa.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    @Mappings({
            @Mapping(source = "addNewUser.prefMethodOfContact", target = "contactMethod")
    })
    User toUserProfile(AddNewUser addNewUser);
}

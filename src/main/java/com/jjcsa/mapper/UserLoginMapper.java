package com.jjcsa.mapper;

import com.jjcsa.dto.AddNewUser;
import com.jjcsa.model.UserLogin;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface UserLoginMapper {
    UserLogin toUserLogin(AddNewUser addNewUser);
}

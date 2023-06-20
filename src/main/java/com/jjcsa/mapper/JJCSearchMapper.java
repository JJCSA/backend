package com.jjcsa.mapper;

import com.jjcsa.dto.AddNewUser;
import com.jjcsa.dto.JJCSearchDTO;
import com.jjcsa.dto.UserDTO;
import com.jjcsa.model.JJCSearch;
import com.jjcsa.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface JJCSearchMapper {

    JJCSearchDTO toJJCSearchDTO(JJCSearch jjcSearch);
}

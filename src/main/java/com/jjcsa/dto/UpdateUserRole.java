package com.jjcsa.dto;

import com.jjcsa.model.enumModel.UserRole;
import lombok.Data;

import javax.validation.Valid;

@Data
public class UpdateUserRole {
    @Valid
    public UserRole role;
}

package com.jjcsa.dto;

import com.jjcsa.model.enumModel.UserStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateUserStatusDto {
    @NotBlank(message = "userId cannot be empty")
    private String userId;

    @NotBlank(message = "status should be a valid UserStatus -> Pending, NewUser, Active or Rejected")
    private UserStatus status;
}

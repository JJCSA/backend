package com.jjcsa.dto;

import com.jjcsa.model.enumModel.UserStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateUserStatusDto {
    @NotBlank
    private String userId;

    @NotBlank
    private UserStatus status;
}

package com.jjcsa.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class UpdateUserPictureDto {
    @NotNull(message = "Picture cannot be null")
    private MultipartFile profPicture;
}

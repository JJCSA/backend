package com.jjcsa.dto;
import com.jjcsa.model.enumModel.UserStatus;

public interface IUserStatusCountDTO {
    int getUserStatusCount();
    UserStatus getUserStatus();
}
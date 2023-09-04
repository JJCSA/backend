package com.jjcsa.controller.admin;

import com.jjcsa.dto.IUserStatusCountDTO;
import com.jjcsa.service.UserDataAnalysisService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Data
@RequestMapping(path="/api/admin/users", produces = "application/json")
@RequiredArgsConstructor
public class AdminUserDataAnalysisController {

    private final UserDataAnalysisService userDataAnalysisService;

    /**
     * User Status Count method gives back the number of users per status
     * @returns a map of userStatus and Integer
     */
    @GetMapping(path="/userStatusCount")
    public List<IUserStatusCountDTO> getUserStatusCount(){
        log.info("Getting number of users per status");
        return userDataAnalysisService.getUserStatusCount();
    }
}

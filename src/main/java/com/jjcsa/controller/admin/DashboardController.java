package com.jjcsa.controller.admin;

import com.jjcsa.dto.IUserStatusCountDTO;
import com.jjcsa.service.DashboardService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Data
@RequestMapping(path="/api/admin/dashboard", produces = "application/json")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;
    @GetMapping(path="/userStatusCount")
    public List<IUserStatusCountDTO> getUserStatusCount(){
        log.info("Getting number of users per status");
        return dashboardService.getUserStatusCount();
    }
}

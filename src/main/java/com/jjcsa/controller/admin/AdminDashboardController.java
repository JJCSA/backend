package com.jjcsa.controller.admin;

import com.jjcsa.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


@Slf4j
@RestController
@Data
@RequestMapping(path="/api/admin/dashboard", produces = "application/json")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserService userService;
    @GetMapping(path = "")
    public Map<String, Integer> getStatusCountForAnalytics() {
        return userService.getStatusCountForAnalytics();
    }
}

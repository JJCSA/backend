package com.jjcsa.controller.user;

import com.jjcsa.dto.JjcSearchDto;
import com.jjcsa.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/jjcsearch")
public class JJCSearchController {
    private final UserService userService;

    @GetMapping()
    public List<JjcSearchDto> getJJCSearchData(){
        return userService.getAllJJCSearchUsers();
    }



}

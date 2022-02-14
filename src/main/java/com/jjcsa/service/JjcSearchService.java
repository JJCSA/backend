package com.jjcsa.service;


import com.jjcsa.dto.JjcSearchDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class JjcSearchService {

    private final UserService userService;

    public List<JjcSearchDto> getJJCSearchData(){
        List<JjcSearchDto> list = userService.getAllJJCSearchUsers();
        return list;
    }


}

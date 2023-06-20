package com.jjcsa.controller.user;

import com.jjcsa.dto.JJCSearchDTO;
import com.jjcsa.model.JJCSearch;
import com.jjcsa.service.JJCSearchService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/search")
public class JJCSearchController {

    @NonNull
    private final JJCSearchService jjcSearchService;

    @GetMapping
    public List<JJCSearchDTO> searchJJCUsers(){
        log.info("Invoking JJC Search!");
        return jjcSearchService.invokeJJCSearch();
    }

}

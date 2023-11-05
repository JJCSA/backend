package com.jjcsa.controller;


import com.jjcsa.service.DataInitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

// Controller class that initiates seed data
// This is created only to support local development

@Profile({"local"}) // local profile only
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/init-data")
public class DataInitController {

    private final DataInitService dataInitService;

    @PostMapping
    public Boolean initData() throws IOException {
        return dataInitService.initData();
    }
}

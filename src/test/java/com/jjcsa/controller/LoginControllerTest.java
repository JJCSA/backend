package com.jjcsa.controller;

import com.jjcsa.service.NewEmailService;
import com.jjcsa.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LoginControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private NewEmailService newEmailSenderService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test2() throws Exception {
        mockMvc.perform(get("/api/users/test2"))
                .andExpect(status().isOk())
                .andExpect(content().string("test2"));
    }
}

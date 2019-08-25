package com.github.pavelvil.springboottest.web.controller;

import com.github.pavelvil.springboottest.AbstractTest;
import com.github.pavelvil.springboottest.model.dto.RegisterData;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserControllerTest extends AbstractTest {

    @Test
    public void register() throws Exception {
        Long oldCount = userService.count();
        RegisterData registerData = new RegisterData("pass", "email@mail.ru");
        String jsonRegisterData = objectMapper.writeValueAsString(registerData);

        userController.perform(post("/api/register")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(jsonRegisterData))
            .andExpect(status().isCreated());

        assertNotEquals(oldCount, userService.count());
    }

}
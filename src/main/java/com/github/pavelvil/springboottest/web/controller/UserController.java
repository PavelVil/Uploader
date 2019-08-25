package com.github.pavelvil.springboottest.web.controller;

import com.github.pavelvil.springboottest.model.User;
import com.github.pavelvil.springboottest.model.dto.RegisterData;
import com.github.pavelvil.springboottest.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
@Log4j2
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@RequestBody RegisterData props) {
        User user = userService.signup(props.getPassword(), props.getEmail());
        log.info(String.format("User register: %s", user));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}

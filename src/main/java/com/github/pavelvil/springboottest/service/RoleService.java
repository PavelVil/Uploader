package com.github.pavelvil.springboottest.service;

import com.github.pavelvil.springboottest.model.security.Role;
import org.springframework.stereotype.Service;

@Service
public interface RoleService {

    String USER_ROLE = "USER_ROLE";

    Role getByName(String roleName);

    void create(Role role);

}

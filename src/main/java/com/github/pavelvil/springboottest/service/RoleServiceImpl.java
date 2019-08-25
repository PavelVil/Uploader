package com.github.pavelvil.springboottest.service;

import com.github.pavelvil.springboottest.model.security.Role;
import com.github.pavelvil.springboottest.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(final RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getByName(final String roleName) {
        return roleRepository.findByName(roleName);
    }

    @Override
    public void create(final Role role) {
        roleRepository.save(role);
    }
}

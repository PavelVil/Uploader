package com.github.pavelvil.springboottest.repository;

import com.github.pavelvil.springboottest.model.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

}

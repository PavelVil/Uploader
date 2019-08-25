package com.github.pavelvil.springboottest.repository;

import com.github.pavelvil.springboottest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);

    User findByEmail(String email);
}

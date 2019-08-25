package com.github.pavelvil.springboottest.service;

import com.github.pavelvil.springboottest.model.File;
import com.github.pavelvil.springboottest.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User signup(String password, String email);

    User save(User user);

    Optional<User> findById(String id);

    List<User> findAll();

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    void remove(String id);

    Optional<File> findUserFile(User user, File file);

    boolean shareFile(User owner, User receiver, File file);

    Long count();

}

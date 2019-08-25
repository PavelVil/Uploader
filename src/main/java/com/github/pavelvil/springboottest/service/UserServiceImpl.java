package com.github.pavelvil.springboottest.service;

import com.github.pavelvil.springboottest.model.File;
import com.github.pavelvil.springboottest.model.User;
import com.github.pavelvil.springboottest.model.security.Role;
import com.github.pavelvil.springboottest.model.security.UserRole;
import com.github.pavelvil.springboottest.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(final UserRepository userRepository, final RoleService roleService, final BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User signup(final String password, final String email) {
        User user = userRepository.findByUsername(email);

        if (user != null) {
            throw new IllegalArgumentException(String.format("User with username '%s' already exist", email));
        }

        user = new User();
        user.setUsername(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setUserRoles(createBasicUserRoles(user));

        return save(user);
    }

    private Set<UserRole> createBasicUserRoles(User user) {
        Set<UserRole> userRoles = new HashSet<>();
        Role role = roleService.getByName(RoleService.USER_ROLE);
        if (role == null) {
            role = new Role();
            role.setName(RoleService.USER_ROLE);
            roleService.create(role);
        }
        UserRole userRole = new UserRole();
        userRole.setRole(role);
        userRole.setUser(user);
        userRoles.add(userRole);

        return userRoles;
    }

    @Override
    public User save(final User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(final String id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findByUsername(final String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    @Override
    public Optional<User> findByEmail(final String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    @Override
    public void remove(final String id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<File> findUserFile(final User user, final File file) {
        return user.getFile(file);
    }

    @Override
    public boolean shareFile(final User owner, final User receiver, final File file) {
        if (!owner.ownFileExist(file) || receiver.fileIsPresent(file)) {
            return false;
        }

        receiver.addSharedFile(file);
        save(receiver);

        return true;
    }

    @Override
    public Long count() {
        return userRepository.count();
    }
}

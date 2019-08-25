package com.github.pavelvil.springboottest.service;

import com.github.pavelvil.springboottest.AbstractTest;
import com.github.pavelvil.springboottest.model.File;
import com.github.pavelvil.springboottest.model.User;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UserServiceImplTest extends AbstractTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void signupUser() throws Exception {
        Long oldCount = userService.count();

        User user = userService.signup("12345", "test@mail.ru");

        assertNotNull(user.getId());
        assertNotEquals(oldCount, userService.count());
    }

    @Test
    public void signupUserFail() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(String.format("User with username '%s' already exist", FIRST_USER_EMAIL));
        userService.signup(FIRST_USER_PASSWORD, FIRST_USER_EMAIL);
    }

    @Test
    public void saveUser() throws Exception {
        Long oldCount = userService.count();

        User user = new User();
        user.setUsername("username");
        user.setEmail("email1234@mail.ru");
        user.setPassword("12345");
        userService.save(user);

        assertNotEquals(oldCount, userService.count());
    }

    @Test
    public void findById() throws Exception {
        User user = createMockUser();

        assertNull(user.getId());

        user = userService.save(user);

        assertNotNull(user.getId());
        assertNotNull(userService.findById(user.getId()));
    }

    @Test
    public void findAll() throws Exception {
        assertEquals(userService.findAll().size(), userService.count().intValue());
    }

    @Test
    public void findByUsername() throws Exception {
        Optional<User> user = userService.findByUsername(FIRST_USER_EMAIL);

        assertTrue(user.isPresent());
        assertNotNull(user.get().getId());
    }

    @Test
    public void findByEmail() throws Exception {
        Optional<User> user = userService.findByEmail(FIRST_USER_EMAIL);

        assertTrue(user.isPresent());
        assertNotNull(user.get().getId());
    }

    @Test
    public void remove() throws Exception {
        User user = createMockUser();
        userService.save(user);

        assertEquals(3, userService.count().intValue());

        userService.remove(user.getId());

        assertEquals(2, userService.count().intValue());
    }

    @Test
    public void findUserFile() throws Exception {
        Optional<File> optionalFile = fileService.findByName(FIRST_FILE_NAME);
        Optional<User> optionalUser = userService.findByUsername(FIRST_USER_EMAIL);

        assertTrue(optionalFile.isPresent());
        assertTrue(optionalUser.isPresent());

        File file = optionalFile.get();
        User user = optionalUser.get();

        assertTrue(userService.findUserFile(user, file).isPresent());
    }

    @Test
    public void shareFile() throws Exception {
        Optional<User> firstOptionalUser = userService.findByUsername(FIRST_USER_EMAIL);
        Optional<User> secondOptionalUser = userService.findByUsername(SECOND_USER_EMAIL);
        Optional<File> optionalFileFromFirstUser = fileService.findByName(FIRST_FILE_NAME);

        assertTrue(firstOptionalUser.isPresent());
        assertTrue(secondOptionalUser.isPresent());
        assertTrue(optionalFileFromFirstUser.isPresent());

        User firstUser = firstOptionalUser.get();
        User secondUser = secondOptionalUser.get();
        File fileFromFirstUser = optionalFileFromFirstUser.get();

        assertTrue(firstUser.fileIsPresent(fileFromFirstUser));
        assertEquals(0, firstUser.getSharedFiles().size());
        assertEquals(0, secondUser.getSharedFiles().size());

        userService.shareFile(firstUser, secondUser, fileFromFirstUser);

        secondUser = userService.findById(secondUser.getId()).get();

        assertTrue(secondUser.fileIsPresent(fileFromFirstUser));
        assertEquals(1, secondUser.getSharedFiles().size());
    }

}
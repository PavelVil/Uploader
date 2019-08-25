package com.github.pavelvil.springboottest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pavelvil.springboottest.config.TestBasicConfiguration;
import com.github.pavelvil.springboottest.config.TestJPAConfiguration;
import com.github.pavelvil.springboottest.model.File;
import com.github.pavelvil.springboottest.model.User;
import com.github.pavelvil.springboottest.repository.FileRepository;
import com.github.pavelvil.springboottest.repository.RoleRepository;
import com.github.pavelvil.springboottest.repository.UserRepository;
import com.github.pavelvil.springboottest.service.FileService;
import com.github.pavelvil.springboottest.service.FileServiceImpl;
import com.github.pavelvil.springboottest.service.RoleService;
import com.github.pavelvil.springboottest.service.RoleServiceImpl;
import com.github.pavelvil.springboottest.service.UserService;
import com.github.pavelvil.springboottest.service.UserServiceImpl;
import com.github.pavelvil.springboottest.web.controller.UserController;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RunWith(SpringRunner.class)
@SpringBootTest()
@EnableWebMvc
@ContextConfiguration(classes = {TestBasicConfiguration.class, TestJPAConfiguration.class})
@Transactional(value = "testJpaTransactionManager")
@EnableJpaRepositories(basePackageClasses = {UserRepository.class, FileRepository.class, RoleRepository.class},
    entityManagerFactoryRef = "entityManagerFactoryBean",
    transactionManagerRef = "testJpaTransactionManager")
public abstract class AbstractTest {

    public static final String FIRST_USER_EMAIL = "first@email.com";

    public static final String FIRST_USER_PASSWORD = "first12345";

    public static final String SECOND_USER_EMAIL = "second@email.com";

    public static final String SECOND_USER_PASSWORD = "second12345";

    public static final String FIRST_FILE_NAME = "firstFile.txt";

    public static final String SECOND_FILE_NAME = "secondFile.txt";

    public static final byte[] FIRST_FILE_DATA = {1, 2, 3};

    public static final byte[] SECOND_FILE_DATA = {4, 5, 6};

    public static final String MOCK_EMAIL = "mock@mock.com";

    public static final String MOCK_PASSWORD = "12345";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    protected UserService userService;

    protected FileService fileService;

    protected MockMvc userController;

    @Autowired
    protected ObjectMapper objectMapper;

    @Before
    public void prepareData() {
        final RoleService roleService = new RoleServiceImpl(roleRepository);
        userService = new UserServiceImpl(userRepository, roleService, bCryptPasswordEncoder);
        fileService = new FileServiceImpl(fileRepository);

        userController = MockMvcBuilders
            .standaloneSetup(new UserController(userService))
            .build();

        populateData();
    }

    //    TODO: change this to a population of data through sql
    private void populateData() {
        User firstUser = userService.signup(FIRST_USER_PASSWORD, FIRST_USER_EMAIL);
        User secondUser = userService.signup(SECOND_USER_PASSWORD, SECOND_USER_EMAIL);

        File firstFile = new File();
        firstFile.setData(FIRST_FILE_DATA);
        firstFile.setName(FIRST_FILE_NAME);
        firstFile.setOwned(firstUser);
        fileService.save(firstFile);

        File secondFile = new File();
        secondFile.setData(SECOND_FILE_DATA);
        secondFile.setName(SECOND_FILE_NAME);
        secondFile.setOwned(secondUser);
        fileService.save(secondFile);

        firstUser.addOwnFile(firstFile);
        userService.save(firstUser);

        secondUser.addOwnFile(secondFile);
        userService.save(secondUser);
    }

    public User createMockUser() {
        User user = new User();
        user.setUsername(MOCK_EMAIL);
        user.setEmail(MOCK_EMAIL);
        user.setPassword(MOCK_PASSWORD);
        return user;
    }

}

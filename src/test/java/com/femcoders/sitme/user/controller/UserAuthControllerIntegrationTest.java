package com.femcoders.sitme.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.femcoders.sitme.user.Role;
import com.femcoders.sitme.user.User;
import com.femcoders.sitme.user.dtos.login.LoginRequest;
import com.femcoders.sitme.user.dtos.register.RegisterRequest;
import com.femcoders.sitme.user.repository.UserRepository;
import com.femcoders.sitme.utils.ApiSuccessResponseTestHelper;
import com.femcoders.sitme.utils.UserTestHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserAuthControllerIntegrationTest {
    private static final String REGISTER_URL = "/api/auth/register";
    private static final String LOGIN_URL = "/api/auth/login";
    private static final String TEST_USERNAME = "Lara Pla";
    private static final String TEST_EMAIL = "lara@sitme.com";
    private static final String TEST_PASSWORD = "Password123.";
    private static final Role TEST_ROLE = Role.USER;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserTestHelper userTestHelper;

    private ApiSuccessResponseTestHelper apiHelper;

    @BeforeEach
    void setUp() {
        apiHelper = new ApiSuccessResponseTestHelper(mockMvc, objectMapper);
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /register | should add a new user successfully")
    void addUser_WhenValidData_ReturnsOkAndRegisteredUser() throws Exception{
        RegisterRequest newUser = new RegisterRequest(
                TEST_USERNAME,
                TEST_EMAIL,
                TEST_PASSWORD
        );

        apiHelper.performRequest(post(REGISTER_URL), newUser, "User registered successfully", HttpStatus.CREATED)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.data.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.data.role").value(TEST_ROLE.toString()));

        assertTrue(userRepository.existsByEmail(TEST_EMAIL));
        assertTrue(userRepository.existsByUsername(TEST_USERNAME));

        User savedUser = userRepository.findByEmail(TEST_EMAIL).orElseThrow();
        assertNotEquals(TEST_PASSWORD, savedUser.getPassword());
        assertTrue(bCryptPasswordEncoder.matches(TEST_PASSWORD, savedUser.getPassword()));
    }

    @Test
    @DisplayName("POST /register | should return 409 when email already exists")
    void addUser_WhenEmailExists_Returns409() throws Exception {
        userTestHelper.existingUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, TEST_ROLE);

        RegisterRequest duplicateEmailUser = new RegisterRequest(
                "New User",
                TEST_EMAIL,
                TEST_PASSWORD
        );
    }

    @Test
    @DisplayName("POST /register - should return 409 when username already exists")
    void addUser_WhenUsernameExists_ReturnsConflict() throws Exception {

        userTestHelper.existingUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, TEST_ROLE);

        RegisterRequest duplicateUsernameUser = new RegisterRequest(
                TEST_USERNAME,
                "different.email@sitme.com",
                TEST_PASSWORD
        );

        apiHelper.performErrorRequest(post(REGISTER_URL),
                duplicateUsernameUser,
                "AUTH_03",
                409,
                "Username already exists"
        );
    }

    @Test
    @DisplayName("POST /register - should return 400 for invalid email")
    void addUser_WhenInvalidEmail_ReturnsBadRequest() throws Exception {

        RegisterRequest invalidEmailUser = new RegisterRequest(
                TEST_USERNAME,
                "invalid email",
                TEST_PASSWORD
        );

        apiHelper.performErrorRequest(post(REGISTER_URL),
                invalidEmailUser,
                "VALIDATION_01",
                400,
                "Email is not valid"
        );
    }

    @Test
    @DisplayName("POST /register - should return 400 for weak password")
    void addUser_WhenWeakPassword_ReturnsBadRequest() throws Exception {

        RegisterRequest weakPasswordUser = new RegisterRequest(
                TEST_USERNAME,
                TEST_EMAIL,
                "weak-password"
        );

        apiHelper.performErrorRequest(post(REGISTER_URL),
                weakPasswordUser,
                "VALIDATION_01",
                400,
                "Password must contain a minimum of 12 characters, including a number, one uppercase letter, one lowercase letter and one special character"
        );
    }

    @ParameterizedTest(name = "Login with identifier={0}")
    @DisplayName("POST /login | should login a user successfully")
    @ValueSource(strings = {TEST_EMAIL, TEST_USERNAME})
    void login_WhenValidIdentifierAndPassword_ReturnsToken(String identifier) throws Exception{
        userTestHelper.existingUser(
                TEST_USERNAME,
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_ROLE
        );

        LoginRequest loginRequest = new LoginRequest(identifier, TEST_PASSWORD);

        apiHelper.performRequest(post(LOGIN_URL), loginRequest, "Login successful", HttpStatus.OK)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isString())
                .andExpect(jsonPath("$.data.token").value(notNullValue()));
    }

    @Test
    @DisplayName("POST /login - should return 404 for nonexistent user")
    void login_WhenUserNotFound_ReturnsUnauthorized() throws Exception {

        LoginRequest loginRequest = new LoginRequest("Nonexistent@sitme.com", TEST_PASSWORD);

        apiHelper.performErrorRequest(post(LOGIN_URL),
                loginRequest,
                "NOT_FOUND",
                404,
                "not found"
        );
    }

    @Test
    @DisplayName("POST /login - should return 401 for wrong password")
    void login_WhenPasswordIsIncorrect_ReturnsUnauthorized() throws Exception {

        userTestHelper.existingUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, TEST_ROLE);

        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, "WrongPassword123.");

        apiHelper.performErrorRequest(post(LOGIN_URL),
                loginRequest,
                "AUTH_02",
                401,
                "Invalid credentials"
        );
    }

    @Test
    @DisplayName("POST /login - should return 400 for empty identifier")
    void login_WhenEmptyIdentifier_ReturnsBadRequest() throws Exception {

        LoginRequest loginRequest = new LoginRequest("", TEST_PASSWORD);

        apiHelper.performErrorRequest(post(LOGIN_URL),
                loginRequest,
                "VALIDATION_01",
                400,
                "Username or e-mail is required"
        );
    }

    @Test
    @DisplayName("POST /login - should return 400 for empty password")
    void login_WhenEmptyPassword_ReturnsBadRequest() throws Exception {

        LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, "");

        apiHelper.performErrorRequest(post(LOGIN_URL),
                loginRequest,
                "VALIDATION_01",
                400,
                "Password is required"
        );
    }
}

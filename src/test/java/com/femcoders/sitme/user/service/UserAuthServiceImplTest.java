package com.femcoders.sitme.user.service;

import com.femcoders.sitme.security.jwt.JwtService;
import com.femcoders.sitme.security.userdetails.CustomUserDetails;
import com.femcoders.sitme.user.Role;
import com.femcoders.sitme.user.User;
import com.femcoders.sitme.user.dtos.login.LoginRequest;
import com.femcoders.sitme.user.dtos.login.LoginResponse;
import com.femcoders.sitme.user.dtos.register.RegisterRequest;
import com.femcoders.sitme.user.dtos.register.RegisterResponse;
import com.femcoders.sitme.user.repository.UserRepository;
import com.femcoders.sitme.user.services.UserAuthServiceImpl;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAuthServiceImplTest {
    private User testUser;
    private RegisterRequest testRegisterRequest;
    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USERNAME = "Lara Pla";
    private static final String TEST_EMAIL = "lara@sitme.com";
    private static final String TEST_PASSWORD = "Password123.";
    private static final String ENCODED_PASSWORD = "$2a$10$EncodedPasswordHash";
    private static final String TEST_JWT_TOKEN = "jwt.token.example";
    private static final Role TEST_ROLE = Role.USER;
    private static final String TEST_IDENTIFIER_USERNAME = "Lara Pla";
    private static final String TEST_IDENTIFIER_EMAIL = "lara@sitme.com";

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

//    @Mock
//    private EmailService emailService;

    @InjectMocks
    private UserAuthServiceImpl userAuthServiceImpl;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setUsername(TEST_USERNAME);
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword(ENCODED_PASSWORD);
        testUser.setRole(TEST_ROLE);

        testRegisterRequest = new RegisterRequest(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);
    }

    @Test
    @DisplayName("POST /register | should add a new user successfully")
    void addUser_WhenValidData_ReturnsOkAndCreateNewUser() {
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(userRepository.existsByUsername(TEST_USERNAME)).thenReturn(false);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
//        doNothing().when(emailService).sendWelcomeNotification(TEST_EMAIL, TEST_USERNAME);

        RegisterResponse result = userAuthServiceImpl.addUser(testRegisterRequest);

        assertNotNull(result);
        assertEquals(TEST_USERNAME, result.username());
        assertEquals(TEST_EMAIL, result.email());

        verify(userRepository).existsByUsername(TEST_USERNAME);
        verify(userRepository).existsByEmail(TEST_EMAIL);
        verify(passwordEncoder).encode(TEST_PASSWORD);
        verify(userRepository).save(any(User.class));
//        verify(emailService).sendWelcomeNotification(TEST_EMAIL, TEST_USERNAME);
    }

    @ParameterizedTest(name = "Login with identifier={0}")
    @DisplayName("POST /login - should login a user successfully")
    @ValueSource(strings = {TEST_IDENTIFIER_EMAIL, TEST_IDENTIFIER_USERNAME})
    void login_WhenValidIdentifier_ReturnsToken(String identifier) {

        Authentication mockAuthentication = mock(Authentication.class);
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        LoginRequest loginRequest = new LoginRequest(identifier, TEST_PASSWORD);

        when(authenticationManager.authenticate(any()))
                .thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
        when(jwtService.generateToken(mockUserDetails)).thenReturn(TEST_JWT_TOKEN);

        LoginResponse result = userAuthServiceImpl.login(loginRequest);

        assertNotNull(result);
        assertEquals(TEST_JWT_TOKEN, result.token());
        assertNotNull(result.token(), "Token should not be null");
        assertFalse(result.token().isBlank(), "Token should not be empty");

        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken(mockUserDetails);
    }
}

package com.femcoders.sitme.user.service;

import com.femcoders.sitme.email.EmailService;
import com.femcoders.sitme.security.jwt.JwtService;
import com.femcoders.sitme.security.userdetails.CustomUserDetails;
import com.femcoders.sitme.user.Role;
import com.femcoders.sitme.user.User;
import com.femcoders.sitme.user.dtos.login.LoginRequest;
import com.femcoders.sitme.user.dtos.login.LoginResponse;
import com.femcoders.sitme.user.dtos.register.RegisterMapper;
import com.femcoders.sitme.user.dtos.register.RegisterRequest;
import com.femcoders.sitme.user.dtos.register.RegisterResponse;
import com.femcoders.sitme.user.exceptions.IdentifierAlreadyExistsException;
import com.femcoders.sitme.user.exceptions.InvalidCredentialsException;
import com.femcoders.sitme.user.exceptions.UserNameNotFoundException;
import com.femcoders.sitme.user.repository.UserRepository;
import com.femcoders.sitme.user.services.UserAuthServiceImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Mock
    private EmailService emailService;

    @Mock
    private RegisterMapper registerMapper;

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
    @DisplayName("POST /register - should add a new user successfully")
    void addUser_WhenValidData_ReturnsOkAndCreateNewUser() {
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(userRepository.existsByUsername(TEST_USERNAME)).thenReturn(false);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(emailService).sendRegistrationEmail(TEST_EMAIL, TEST_USERNAME);
        when(registerMapper.dtoToEntity(any(RegisterRequest.class))).thenReturn(testUser);
        when(registerMapper.entityToDto(any(User.class))).thenReturn(
                new RegisterResponse(TEST_USERNAME, TEST_EMAIL, TEST_ROLE)
        );

        RegisterResponse result = userAuthServiceImpl.addUser(testRegisterRequest);

        assertNotNull(result);
        assertEquals(TEST_USERNAME, result.username());
        assertEquals(TEST_EMAIL, result.email());

        verify(userRepository).existsByUsername(TEST_USERNAME);
        verify(userRepository).existsByEmail(TEST_EMAIL);
        verify(passwordEncoder).encode(TEST_PASSWORD);
        verify(userRepository).save(any(User.class));
        verify(emailService).sendRegistrationEmail(TEST_EMAIL, TEST_USERNAME);
    }

    @ParameterizedTest(name = "Register throw exception with identifier={0}")
    @DisplayName("POST /register - should throw IdentifierAlreadyExistsException when identifier already exists")
    @ValueSource(strings = {TEST_IDENTIFIER_EMAIL, TEST_IDENTIFIER_USERNAME})
    void addUser_WhenIdentifierAlreadyExists_ThrowsException(String identifier) {
        if (identifier.equals(TEST_IDENTIFIER_EMAIL)) {
            when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);
        }

        if (identifier.equals(TEST_IDENTIFIER_USERNAME)) {
            when(userRepository.existsByUsername(TEST_USERNAME)).thenReturn(true);
        }

        IdentifierAlreadyExistsException exception = assertThrows(
                IdentifierAlreadyExistsException.class,
                () -> userAuthServiceImpl.addUser(testRegisterRequest)
        );

        if (identifier.equals(TEST_IDENTIFIER_EMAIL)) {
            assertTrue(exception.getMessage().toLowerCase().contains("email"));
            verify(userRepository).existsByEmail(TEST_EMAIL);
        }

        if (identifier.equals(TEST_IDENTIFIER_USERNAME)) {
            assertTrue(exception.getMessage().toLowerCase().contains("username"));
            verify(userRepository).existsByUsername(TEST_USERNAME);
        }

        verify(userRepository, never()).save(any());
    }

    static Stream<Arguments> invalidRegisterRequest() {
        return Stream.of(
                Arguments.of(new RegisterRequest("", TEST_EMAIL, TEST_PASSWORD),
                        "Username is required"),
                Arguments.of(new RegisterRequest("T",TEST_EMAIL, TEST_PASSWORD),
                        "Username must be between 2 and 50 characters"),
                Arguments.of(new RegisterRequest(TEST_USERNAME, "", TEST_PASSWORD),
                        "Email is required"),
                Arguments.of(new RegisterRequest(TEST_USERNAME, "kkk", TEST_PASSWORD),
                        "Email is not valid"),
                Arguments.of(new RegisterRequest(TEST_USERNAME, TEST_EMAIL, ""),
                        "Password is required"),
                Arguments.of(new RegisterRequest(TEST_USERNAME, TEST_EMAIL, "ggg"),
                        "Password must contain a minimum of 12 characters, including a number, one uppercase letter, one lowercase letter and one special character")
        );
    }

    @ParameterizedTest(name = "{index} -> {1}")
    @MethodSource("invalidRegisterRequest")
    @DisplayName("POST /register - invalid register request")
    void whenInvalidRegisterRequest_ReturnsValidationError(RegisterRequest dto, String expectedMessage) {

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(dto);

        assertTrue(violations
                        .stream().anyMatch(violation -> violation.getMessage().contains(expectedMessage)),
                () -> "Expected violation containing: " + expectedMessage);
    }

    @ParameterizedTest(name = "Login with identifier={0}")
    @DisplayName("POST /login - should login a user successfully")
    @ValueSource(strings = {TEST_IDENTIFIER_EMAIL, TEST_IDENTIFIER_USERNAME})
    void login_WhenValidIdentifier_ReturnsToken(String identifier) {

        Authentication mockAuthentication = mock(Authentication.class);
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        LoginRequest loginRequest = new LoginRequest(identifier, TEST_PASSWORD);

        lenient().when(userRepository.existsByUsername(anyString())).thenReturn(true);
        lenient().when(userRepository.existsByEmail(anyString())).thenReturn(true);

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

    @Test
    @DisplayName("POST /login - should return user not found error 404")
    void login_WhenUserNotFound_ThrowsUserNotFoundException(){

        LoginRequest loginRequest = new LoginRequest(TEST_IDENTIFIER_USERNAME, TEST_PASSWORD);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        UserNameNotFoundException exception = assertThrows(
                UserNameNotFoundException.class,
                () -> userAuthServiceImpl.login(loginRequest));

        assertTrue(exception.getMessage().contentEquals("Username " + TEST_IDENTIFIER_USERNAME + " not found"));
        assertTrue(exception.getMessage().contains("not found"));

        verifyNoInteractions(authenticationManager);
    }

    @Test
    @DisplayName("POST /login - should return invalid credentials error 401")
    void login_WhenInvalidCredentials_ThrowsInvalidCredentialsException(){

        LoginRequest loginRequest = new LoginRequest(TEST_IDENTIFIER_EMAIL, TEST_PASSWORD);

        lenient().when(userRepository.existsByUsername(anyString())).thenReturn(true);
        lenient().when(userRepository.existsByEmail(anyString())).thenReturn(true);

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userAuthServiceImpl.login(loginRequest));

        assertTrue(exception.getMessage().contains("Invalid credentials for: " + loginRequest.identifier()));

        verify(authenticationManager).authenticate(any());
    }

    static Stream<Arguments> invalidLoginRequest() {
        return Stream.of(
                Arguments.of(new LoginRequest("", TEST_PASSWORD),
                        "Username or e-mail is required"),
                Arguments.of(new LoginRequest(null, TEST_PASSWORD),
                        "Username or e-mail is required"),
                Arguments.of(new LoginRequest(TEST_IDENTIFIER_USERNAME, ""),
                        "Password is required"),
                Arguments.of(new LoginRequest(TEST_IDENTIFIER_EMAIL, null),
                        "Password is required")
        );
    }

    @ParameterizedTest(name = "{index} -> {1}")
    @MethodSource("invalidLoginRequest")
    void whenInvalidLoginDto_ReturnsValidationError(LoginRequest dto, String expectedMessage) {

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(dto);

        assertTrue(violations
                        .stream().anyMatch(violation -> violation.getMessage().contains(expectedMessage)),
                () -> "Expected violation containing: " + expectedMessage);
    }
}

package com.femcoders.sitme.user.service;

import com.femcoders.sitme.cloudinary.service.CloudinaryService;
import com.femcoders.sitme.user.User;
import com.femcoders.sitme.user.dtos.user.UserResponse;
import com.femcoders.sitme.user.dtos.user.UserRequest;
import com.femcoders.sitme.user.exceptions.IdentifierAlreadyExistsException;
import com.femcoders.sitme.user.repository.UserRepository;
import com.femcoders.sitme.user.services.user.UserServiceImpl;
import com.femcoders.sitme.shared.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USERNAME = "Mayleris Echezuria";
    private static final String TEST_EMAIL = "may@sitme.com";
    private static final String TEST_PASSWORD = "Password123.";
    private static final String ENCODED_PASSWORD = "$2a$10$EncodedPasswordHash";
    private static final String UPDATED_USERNAME = "Mayleris Updated";
    private static final String UPDATED_EMAIL = "may.updated@sitme.com";
    private static final String UPDATED_PASSWORD = "NewPassword123.";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private MultipartFile mockFile;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserRequest testUserRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setUsername(TEST_USERNAME);
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword(ENCODED_PASSWORD);

        testUserRequest = new UserRequest(
                UPDATED_USERNAME,
                UPDATED_EMAIL,
                UPDATED_PASSWORD
        );
    }

    @Test
    @DisplayName("GET /users - should return all users")
    void getAllUsers_WhenUsersExist_ReturnsUserList() {
        List<User> users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<UserResponse> result = userService.getAllUsers();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("GET /users/{id} - should return user by id")
    void getUserById_WhenValidId_ReturnsUser() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));

        UserResponse result = userService.getUserById(TEST_USER_ID);

        assertNotNull(result);
        verify(userRepository).findById(TEST_USER_ID);
    }

    @Test
    @DisplayName("GET /users/{id} - should throw EntityNotFoundException when user not found")
    void getUserById_WhenUserNotFound_ThrowsException() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getUserById(TEST_USER_ID)
        );

        assertTrue(exception.getMessage().contains(User.class.getSimpleName()));
        verify(userRepository).findById(TEST_USER_ID);
    }

    @Test
    @DisplayName("PUT /users/{id} - should update user successfully when username and email don't change")
    void updateUser_WhenSameUsernameAndEmail_ReturnsUpdatedUser() {
        UserRequest sameDataRequest = new UserRequest(TEST_USERNAME, TEST_EMAIL, UPDATED_PASSWORD);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(UPDATED_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse result = userService.updateUser(TEST_USER_ID, sameDataRequest, null);

        assertNotNull(result);

        verify(userRepository, never()).existsByUsername(any());
        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("PUT /users/{id} - should throw exception when new username already exists")
    void updateUser_WhenUsernameExists_ThrowsIdentifierAlreadyExistsException() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername(UPDATED_USERNAME)).thenReturn(true);

        IdentifierAlreadyExistsException exception = assertThrows(
                IdentifierAlreadyExistsException.class,
                () -> userService.updateUser(TEST_USER_ID, testUserRequest, null)
        );

        assertTrue(exception.getMessage().contains("Username is already registered"));
        verify(userRepository).existsByUsername(UPDATED_USERNAME);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("PUT /users/{id} - should throw exception when new email already exists")
    void updateUser_WhenEmailExists_ThrowsIdentifierAlreadyExistsException() {
        UserRequest requestWithSameUsername = new UserRequest(TEST_USERNAME, UPDATED_EMAIL, UPDATED_PASSWORD);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(UPDATED_EMAIL)).thenReturn(true);

        IdentifierAlreadyExistsException exception = assertThrows(
                IdentifierAlreadyExistsException.class,
                () -> userService.updateUser(TEST_USER_ID, requestWithSameUsername, null)
        );

        assertTrue(exception.getMessage().contains("Email is already registered"));
        verify(userRepository).existsByEmail(UPDATED_EMAIL);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("PUT /users/{id} - should update user without password when password is null")
    void updateUser_WhenPasswordIsNull_UpdatesUserWithoutPassword() {
        UserRequest requestWithoutPassword = new UserRequest(UPDATED_USERNAME, UPDATED_EMAIL, null);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername(UPDATED_USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(UPDATED_EMAIL)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse result = userService.updateUser(TEST_USER_ID, requestWithoutPassword, null);

        assertNotNull(result);
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("PUT /users/{id} - should update user with image successfully")
    void updateUser_WhenWithImage_ReturnsUpdatedUserWithImage() {
        when(mockFile.isEmpty()).thenReturn(false);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername(UPDATED_USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(UPDATED_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(UPDATED_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse result = userService.updateUser(TEST_USER_ID, testUserRequest, mockFile);

        assertNotNull(result);
        verify(cloudinaryService).uploadEntityImage(testUser, mockFile, "sitme/users");
        verify(cloudinaryService).deleteEntityImage(testUser);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("PUT /users/{id} - should throw EntityNotFoundException when user not found")
    void updateUser_WhenUserNotFound_ThrowsException() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.updateUser(TEST_USER_ID, testUserRequest, null)
        );

        assertTrue(exception.getMessage().contains(User.class.getSimpleName()));
        verify(userRepository).findById(TEST_USER_ID);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("POST /users/{id}/image - should upload user image successfully")
    void uploadUserImage_WhenValidData_ReturnsUserWithImage() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse result = userService.uploadUserImage(TEST_USER_ID, mockFile);

        assertNotNull(result);
        verify(cloudinaryService).uploadEntityImage(testUser, mockFile, "sitme/users");
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("DELETE /users/{id}/image - should delete user image successfully")
    void deleteUserImage_WhenUserExists_DeletesImage() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        assertDoesNotThrow(() -> userService.deleteUserImage(TEST_USER_ID));

        verify(cloudinaryService).deleteEntityImage(testUser);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("DELETE /users/{id} - should delete user successfully")
    void deleteUser_WhenUserExists_DeletesUser() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(TEST_USER_ID);

        assertDoesNotThrow(() -> userService.deleteUser(TEST_USER_ID));

        verify(userRepository).findById(TEST_USER_ID);
        verify(userRepository).deleteById(TEST_USER_ID);
    }

    @Test
    @DisplayName("DELETE /users/{id} - should throw EntityNotFoundException when user not found")
    void deleteUser_WhenUserNotFound_ThrowsException() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.deleteUser(TEST_USER_ID)
        );

        assertTrue(exception.getMessage().contains(User.class.getSimpleName()));
        assertTrue(exception.getMessage().contains(TEST_USER_ID.toString()));
        verify(userRepository).findById(TEST_USER_ID);
        verify(userRepository, never()).deleteById(any());
    }
}
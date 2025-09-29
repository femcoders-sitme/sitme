package com.femcoders.sitme.user.service;

import com.femcoders.sitme.security.userdetails.CustomUserDetails;
import com.femcoders.sitme.cloudinary.service.CloudinaryService;
import com.femcoders.sitme.user.User;
import com.femcoders.sitme.user.dtos.user.UserResponse;
import com.femcoders.sitme.user.dtos.user.UserUpdateRequest;
import com.femcoders.sitme.user.exceptions.UserNameNotFoundException;
import com.femcoders.sitme.user.repository.UserRepository;
import com.femcoders.sitme.user.services.UserServiceImpl;
import com.femcoders.sitme.shared.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_OTHER_USER_ID = 2L;
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

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails userDetails;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserUpdateRequest testUpdateRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setUsername(TEST_USERNAME);
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword(ENCODED_PASSWORD);

        testUpdateRequest = new UserUpdateRequest(
                UPDATED_USERNAME,
                UPDATED_EMAIL,
                UPDATED_PASSWORD
        );
    }

    @Test
    @DisplayName("PUT /users/{id} - should update user successfully")
    void updateUser_WhenValidData_ReturnsUpdatedUser() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(UPDATED_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse result = userService.updateUser(TEST_USER_ID, testUpdateRequest, null);

        assertNotNull(result);
        assertEquals(UPDATED_USERNAME, testUser.getUsername());
        assertEquals(UPDATED_EMAIL, testUser.getEmail());

        verify(userRepository).findById(TEST_USER_ID);
        verify(passwordEncoder).encode(UPDATED_PASSWORD);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("PUT /users/{id} - should update user with image successfully")
    void updateUser_WhenWithImage_ReturnsUpdatedUserWithImage() {
        when(mockFile.isEmpty()).thenReturn(false);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(UPDATED_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse result = userService.updateUser(TEST_USER_ID, testUpdateRequest, mockFile);

        assertNotNull(result);

        verify(cloudinaryService).deleteEntityImage(testUser);
        verify(cloudinaryService).uploadEntityImage(testUser, mockFile, "sitme/users");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("PUT /users/{id} - should throw UserNameNotFoundException when user not found")
    void updateUser_WhenUserNotFound_ThrowsException() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        UserNameNotFoundException exception = assertThrows(
                UserNameNotFoundException.class,
                () -> userService.updateUser(TEST_USER_ID, testUpdateRequest, null)
        );

        assertTrue(exception.getMessage().contains("with id " + TEST_USER_ID));
        verify(userRepository).findById(TEST_USER_ID);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("PUT /users/{id}/profile - should update profile successfully when admin")
    void updateProfile_WhenAdmin_ReturnsUpdatedProfile() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(UPDATED_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse result = userService.updateProfile(TEST_USER_ID, testUpdateRequest);

        assertNotNull(result);
        assertEquals(UPDATED_USERNAME, testUser.getUsername());

        verify(userRepository).findById(TEST_USER_ID);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("PUT /users/{id}/profile - should update own profile successfully")
    void updateProfile_WhenOwnProfile_ReturnsUpdatedProfile() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(userDetails.getId()).thenReturn(TEST_USER_ID);

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(UPDATED_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse result = userService.updateProfile(TEST_USER_ID, testUpdateRequest);

        assertNotNull(result);
        assertEquals(UPDATED_USERNAME, testUser.getUsername());

        verify(userRepository).findById(TEST_USER_ID);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("PUT /users/{id}/profile - should throw AccessDeniedException when user tries to update other profile")
    void updateProfile_WhenUserTriesToUpdateOtherProfile_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(userDetails.getId()).thenReturn(TEST_OTHER_USER_ID);

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> userService.updateProfile(TEST_USER_ID, testUpdateRequest)
        );

        assertTrue(exception.getMessage().contains("You are not allowed to update this user"));
        verify(userRepository).findById(TEST_USER_ID);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("PUT /users/{id}/profile - should throw EntityNotFoundException when profile not found")
    void updateProfile_WhenProfileNotFound_ThrowsException() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.updateProfile(TEST_USER_ID, testUpdateRequest)
        );

        assertTrue(exception.getMessage().contains("User not found with id " + TEST_USER_ID));
        verify(userRepository).findById(TEST_USER_ID);
        verify(userRepository, never()).save(any());

        verifyNoInteractions(securityContext, authentication, userDetails);
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
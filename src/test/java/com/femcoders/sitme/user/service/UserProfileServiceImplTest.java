package com.femcoders.sitme.user.service;

import com.femcoders.sitme.cloudinary.service.CloudinaryService;
import com.femcoders.sitme.security.userdetails.CustomUserDetails;
import com.femcoders.sitme.user.User;
import com.femcoders.sitme.user.dtos.user.UserRequest;
import com.femcoders.sitme.user.dtos.user.UserResponse;
import com.femcoders.sitme.user.exceptions.IdentifierAlreadyExistsException;
import com.femcoders.sitme.user.repository.UserRepository;
import com.femcoders.sitme.user.services.userprofile.UserProfileServiceImpl;
import com.femcoders.sitme.shared.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserProfileServiceImplTest {

    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_OTHER_USER_ID = 2L;
    private static final String TEST_USERNAME = "Mayleris Echezuria";
    private static final String TEST_EMAIL = "may@sitme.com";
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
    private CustomUserDetails userDetails;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

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
    @DisplayName("GET /profile/me - should return user profile successfully")
    void getMyProfile_WhenValidUser_ReturnsUserProfile() {
        when(userDetails.getId()).thenReturn(TEST_USER_ID);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));

        UserResponse result = userProfileService.getMyProfile(userDetails);

        assertNotNull(result);
        verify(userRepository).findById(TEST_USER_ID);
        verify(userDetails).getId();
    }

    @Test
    @DisplayName("GET /profile/me - should throw EntityNotFoundException when user not found")
    void getMyProfile_WhenUserNotFound_ThrowsException() {
        when(userDetails.getId()).thenReturn(TEST_USER_ID);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userProfileService.getMyProfile(userDetails)
        );

        assertTrue(exception.getMessage().contains("User"));
        verify(userRepository).findById(TEST_USER_ID);
    }

    @Test
    @DisplayName("PUT /profile/me - should update own profile successfully as USER")
    void updateMyProfile_WhenUserUpdatesOwnProfile_ReturnsUpdatedProfile() {
        when(userDetails.getId()).thenReturn(TEST_USER_ID);
        when(userDetails.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername(UPDATED_USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(UPDATED_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(UPDATED_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse result = userProfileService.updateMyProfile(userDetails, testUserRequest, null);

        assertNotNull(result);
        assertEquals(UPDATED_USERNAME, testUser.getUsername());
        assertEquals(UPDATED_EMAIL, testUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("PUT /profile/me - should update own profile successfully as ADMIN")
    void updateMyProfile_WhenAdminUpdatesOwnProfile_ReturnsUpdatedProfile() {
        when(userDetails.getId()).thenReturn(TEST_USER_ID);
        when(userDetails.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername(UPDATED_USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(UPDATED_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(UPDATED_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse result = userProfileService.updateMyProfile(userDetails, testUserRequest, null);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("PUT /profile/me - should update profile without password when password is null")
    void updateMyProfile_WhenPasswordIsNull_UpdatesProfileWithoutPassword() {
        UserRequest requestWithoutPassword = new UserRequest(UPDATED_USERNAME, UPDATED_EMAIL, null);
        when(userDetails.getId()).thenReturn(TEST_USER_ID);
        when(userDetails.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername(UPDATED_USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(UPDATED_EMAIL)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse result = userProfileService.updateMyProfile(userDetails, requestWithoutPassword, null);

        assertNotNull(result);
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("PUT /profile/me - should throw AccessDeniedException when user tries to update other profile")
    void updateMyProfile_WhenUserTriesToUpdateOtherProfile_ThrowsAccessDeniedException() {
        when(userDetails.getId()).thenReturn(TEST_USER_ID);
        when(userDetails.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_USER")));

        User wrongUser = new User();
        wrongUser.setId(TEST_OTHER_USER_ID);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(wrongUser));

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> userProfileService.updateMyProfile(userDetails, testUserRequest, null)
        );

        assertTrue(exception.getMessage().contains("You are not allowed to update this profile"));
    }

    @Test
    @DisplayName("PUT /profile/me - should throw exception when new username already exists")
    void updateMyProfile_WhenUsernameExists_ThrowsIdentifierAlreadyExistsException() {
        when(userDetails.getId()).thenReturn(TEST_USER_ID);
        when(userDetails.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername(UPDATED_USERNAME)).thenReturn(true);

        IdentifierAlreadyExistsException exception = assertThrows(
                IdentifierAlreadyExistsException.class,
                () -> userProfileService.updateMyProfile(userDetails, testUserRequest, null)
        );

        assertTrue(exception.getMessage().contains("Username is already registered"));
        verify(userRepository).existsByUsername(UPDATED_USERNAME);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("PUT /profile/me - should throw exception when new email already exists")
    void updateMyProfile_WhenEmailExists_ThrowsIdentifierAlreadyExistsException() {
        UserRequest requestWithSameUsername = new UserRequest(TEST_USERNAME, UPDATED_EMAIL, UPDATED_PASSWORD);
        when(userDetails.getId()).thenReturn(TEST_USER_ID);
        when(userDetails.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(UPDATED_EMAIL)).thenReturn(true);

        IdentifierAlreadyExistsException exception = assertThrows(
                IdentifierAlreadyExistsException.class,
                () -> userProfileService.updateMyProfile(userDetails, requestWithSameUsername, null)
        );

        assertTrue(exception.getMessage().contains("Email is already registered"));
        verify(userRepository).existsByEmail(UPDATED_EMAIL);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("PUT /profile/me - should update profile with image successfully")
    void updateMyProfile_WhenWithImage_ReturnsUpdatedProfileWithImage() {
        when(mockFile.isEmpty()).thenReturn(false);
        when(userDetails.getId()).thenReturn(TEST_USER_ID);
        when(userDetails.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername(UPDATED_USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(UPDATED_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(UPDATED_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse result = userProfileService.updateMyProfile(userDetails, testUserRequest, mockFile);

        assertNotNull(result);
        verify(cloudinaryService).uploadEntityImage(testUser, mockFile, "sitme/users");
        verify(cloudinaryService).deleteEntityImage(testUser);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("PUT /profile/me - should not validate uniqueness when username and email don't change")
    void updateMyProfile_WhenSameUsernameAndEmail_ReturnsUpdatedProfile() {
        UserRequest sameDataRequest = new UserRequest(TEST_USERNAME, TEST_EMAIL, UPDATED_PASSWORD);
        when(userDetails.getId()).thenReturn(TEST_USER_ID);
        when(userDetails.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(UPDATED_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse result = userProfileService.updateMyProfile(userDetails, sameDataRequest, null);

        assertNotNull(result);
        verify(userRepository, never()).existsByUsername(any());
        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("PUT /profile/me - should throw EntityNotFoundException when profile not found")
    void updateMyProfile_WhenProfileNotFound_ThrowsException() {
        when(userDetails.getId()).thenReturn(TEST_USER_ID);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userProfileService.updateMyProfile(userDetails, testUserRequest, null)
        );

        assertTrue(exception.getMessage().contains("User"));
        verify(userRepository).findById(TEST_USER_ID);
        verify(userRepository, never()).save(any());
    }
}
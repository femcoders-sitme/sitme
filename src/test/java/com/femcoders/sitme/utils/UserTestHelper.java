package com.femcoders.sitme.utils;

import com.femcoders.sitme.user.Role;
import com.femcoders.sitme.user.User;
import com.femcoders.sitme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserTestHelper {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User existingUser(String username, String email, String password, Role role) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();

        return userRepository.save(user);
    }
}

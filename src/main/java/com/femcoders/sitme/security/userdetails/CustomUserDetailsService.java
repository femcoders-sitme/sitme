package com.femcoders.sitme.security.userdetails;

import com.femcoders.sitme.user.User;
import com.femcoders.sitme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {

        Optional<User> user = userRepository.findByUsername(identifier);

        if (user.isEmpty()) {
            user = userRepository.findByEmail(identifier);
        }

        return user.map(userEntity -> new CustomUserDetails(userEntity))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));
    }
}

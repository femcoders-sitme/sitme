package com.femcoders.sitme.user.services;

import com.femcoders.sitme.email.EmailService;
import com.femcoders.sitme.security.jwt.JwtService;
import com.femcoders.sitme.security.userdetails.CustomUserDetails;
import com.femcoders.sitme.user.User;
import com.femcoders.sitme.user.dtos.login.LoginRequest;
import com.femcoders.sitme.user.dtos.login.LoginResponse;
import com.femcoders.sitme.user.dtos.register.RegisterMapper;
import com.femcoders.sitme.user.dtos.register.RegisterRequest;
import com.femcoders.sitme.user.dtos.register.RegisterResponse;
import com.femcoders.sitme.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAuthServiceImpl implements UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Autowired
    private final EmailService emailService;

    @Override
    public RegisterResponse addUser(RegisterRequest registerRequest) {

        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User newUser = RegisterMapper.dtoToEntity(registerRequest);
        newUser.setPassword(passwordEncoder.encode(registerRequest.password()));
        User savedUser = userRepository.save(newUser);
        emailService.sendRegistrationEmail(newUser.getEmail(), newUser.getUsername());

        return RegisterMapper.entityToDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.identifier(),
                            loginRequest.password()
                    )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            return new LoginResponse(token);

        } catch (UsernameNotFoundException exception) {
            throw new EntityNotFoundException("User not found: " + loginRequest.identifier());
        } catch (BadCredentialsException exception) {
            throw new BadCredentialsException("Invalid credentials for: " + loginRequest.identifier());
        }
    }
}

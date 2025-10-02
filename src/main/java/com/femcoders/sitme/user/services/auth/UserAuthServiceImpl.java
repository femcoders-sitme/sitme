package com.femcoders.sitme.user.services.auth;

import com.femcoders.sitme.email.EmailService;
import com.femcoders.sitme.security.jwt.JwtService;
import com.femcoders.sitme.security.userdetails.CustomUserDetails;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAuthServiceImpl implements UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final RegisterMapper registerMapper;

    @Override
    public RegisterResponse addUser(RegisterRequest registerRequest) {

        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new IdentifierAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new IdentifierAlreadyExistsException("Email already registered");
        }

        User newUser = registerMapper.dtoToEntity(registerRequest);
        newUser.setPassword(passwordEncoder.encode(registerRequest.password()));
        User savedUser = userRepository.save(newUser);
        emailService.sendRegistrationEmail(savedUser.getEmail(), savedUser.getUsername());

        return registerMapper.entityToDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {

        boolean userExists = userRepository.existsByUsername(loginRequest.identifier()) || userRepository.existsByEmail(loginRequest.identifier());

        if (!userExists) {
            throw new UserNameNotFoundException(loginRequest.identifier());
        }

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

        } catch (BadCredentialsException exception) {
            throw new InvalidCredentialsException("Invalid credentials for: " + loginRequest.identifier());
        }
    }
}

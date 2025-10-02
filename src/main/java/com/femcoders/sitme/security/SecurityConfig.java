package com.femcoders.sitme.security;

import com.femcoders.sitme.security.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(manager -> manager
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/users/me").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/me").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/spaces").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/spaces/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reservations/me").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/{id}").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/reservations/{id}/cancel").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reservations").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reservations/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/spaces/filter/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/spaces").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/reservations").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/spaces/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/reservations/{id}").hasRole("ADMIN")
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/test/**").permitAll()
                        .anyRequest().authenticated()
                )
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

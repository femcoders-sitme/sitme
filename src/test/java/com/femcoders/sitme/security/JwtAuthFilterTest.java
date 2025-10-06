package com.femcoders.sitme.security;

import com.femcoders.sitme.security.exceptions.ExpiredTokenException;
import com.femcoders.sitme.security.exceptions.InvalidTokenException;
import com.femcoders.sitme.security.jwt.JwtAuthFilter;
import com.femcoders.sitme.security.jwt.JwtService;
import com.femcoders.sitme.security.userdetails.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    private TestableJwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthFilter = new TestableJwtAuthFilter(jwtService, customUserDetailsService);
        SecurityContextHolder.clearContext();
    }

    private static class TestableJwtAuthFilter extends JwtAuthFilter {
        public TestableJwtAuthFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
            super(jwtService, customUserDetailsService);
        }
        public void invokeDoFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
                throws ServletException, IOException {
            super.doFilterInternal(req, res, chain);
        }
    }

    @Test
    void doFilterInternal_NoAuthorizationHeader_CallsFilterChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthFilter.invokeDoFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, customUserDetailsService);
    }

    @Test
    void doFilterInternal_ExpiredJwt_ThrowsExpiredTokenException() {
        when(request.getHeader("Authorization")).thenReturn("Bearer expiredtoken");
        when(jwtService.extractUserId("expiredtoken")).thenThrow(new ExpiredJwtException(null, null, "expired"));

        assertThrows(ExpiredTokenException.class, () ->
                jwtAuthFilter.invokeDoFilterInternal(request, response, filterChain)
        );
    }

    @Test
    void doFilterInternal_MalformedJwt_ThrowsInvalidTokenException() {
        when(request.getHeader("Authorization")).thenReturn("Bearer malformedtoken");
        when(jwtService.extractUserId("malformedtoken")).thenThrow(new MalformedJwtException("bad token"));

        assertThrows(InvalidTokenException.class, () ->
                jwtAuthFilter.invokeDoFilterInternal(request, response, filterChain)
        );
    }

    @Test
    void doFilterInternal_GenericException_ThrowsInvalidTokenException() {
        when(request.getHeader("Authorization")).thenReturn("Bearer generic");
        when(jwtService.extractUserId("generic")).thenThrow(new RuntimeException("error"));

        assertThrows(InvalidTokenException.class, () ->
                jwtAuthFilter.invokeDoFilterInternal(request, response, filterChain)
        );
    }

    @Test
    void doFilterInternal_ValidToken_SetsAuthenticationAndCallsFilterChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer validtoken");
        when(jwtService.extractUserId("validtoken")).thenReturn("1");
        when(customUserDetailsService.loadUserById(1L)).thenReturn(userDetails);
        when(jwtService.isValidToken("validtoken", userDetails)).thenReturn(true);

        jwtAuthFilter.invokeDoFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    void doFilterInternal_InvalidToken_ThrowsInvalidTokenException() {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidtoken");
        when(jwtService.extractUserId("invalidtoken")).thenReturn("2");
        when(customUserDetailsService.loadUserById(2L)).thenReturn(userDetails);
        when(jwtService.isValidToken("invalidtoken", userDetails)).thenReturn(false);

        assertThrows(InvalidTokenException.class, () ->
                jwtAuthFilter.invokeDoFilterInternal(request, response, filterChain)
        );
    }

    @Test
    void doFilterInternal_HeaderWithoutBearer_CallsFilterChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Token something");

        jwtAuthFilter.invokeDoFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, customUserDetailsService);
    }

    @Test
    void doFilterInternal_UserIdNull_DoesNotAuthenticateAndCallsFilterChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer sometoken");
        when(jwtService.extractUserId("sometoken")).thenReturn(null);

        jwtAuthFilter.invokeDoFilterInternal(request, response, filterChain);

        verify(customUserDetailsService, never()).loadUserById(anyLong());
        verify(filterChain).doFilter(request, response);
    }
}


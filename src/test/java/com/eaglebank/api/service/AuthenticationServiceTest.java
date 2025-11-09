package com.eaglebank.api.service;

import com.eaglebank.api.domain.User;
import com.eaglebank.api.dto.auth.LoginRequest;
import com.eaglebank.api.dto.auth.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("usr-123abc");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
    }

    @Test
    void authenticate_ShouldReturnLoginResponse_WhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("test@example.com", "password123");

        when(userService.findByEmail("test@example.com")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken("usr-123abc")).thenReturn("jwt-token");

        LoginResponse response = authenticationService.authenticate(request);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.userId()).isEqualTo("usr-123abc");
    }

    @Test
    void authenticate_ShouldThrowUnauthorized_WhenPasswordDoesNotMatch() {
        LoginRequest request = new LoginRequest("test@example.com", "wrong-password");

        when(userService.findByEmail("test@example.com")).thenReturn(testUser);
        when(passwordEncoder.matches("wrong-password", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authenticationService.authenticate(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void authenticate_ShouldPropagateException_WhenUserServiceThrows() {
        LoginRequest request = new LoginRequest("missing@example.com", "password123");

        when(userService.findByEmail("missing@example.com"))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        assertThatThrownBy(() -> authenticationService.authenticate(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}

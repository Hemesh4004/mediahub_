package com.mediahub.iam.controller;

import com.mediahub.iam.entity.Role;
import com.mediahub.iam.entity.User;
import com.mediahub.iam.enums.UserStatus;
import com.mediahub.iam.service.AuthService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Web Slice Tests")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleId(1L);
        role.setRoleType("subscriber");

        sampleUser = new User();
        sampleUser.setUserId(1L);
        sampleUser.setName("Test User");
        sampleUser.setEmail("test@mediahub.com");
        sampleUser.setRole(role);
        sampleUser.setStatus(UserStatus.active);
        sampleUser.setIsRevoked(false);
    }

    @Test
    @DisplayName("TC-CTRL-AUTH-01: Register returns 201 on success")
    void testRegisterSuccess() {
        when(authService.register(anyString(), anyString(),
            anyString(), anyString(), anyString()))
            .thenReturn(sampleUser);

        var request = new com.mediahub.iam.dto.RegisterRequest();
        request.setName("Test User");
        request.setEmail("test@mediahub.com");
        request.setPassword("Test@123");
        request.setPhone("+91-9111111111");
        request.setCountry("IN");

        ResponseEntity<?> response = authController.register(request);

        assertEquals(201, response.getStatusCode().value());
        verify(authService, times(1)).register(anyString(),
            anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("TC-CTRL-AUTH-02: Register throws when email already exists")
    void testRegisterEmailExists() {
        when(authService.register(anyString(), anyString(),
            anyString(), anyString(), anyString()))
            .thenThrow(new RuntimeException("EMAIL_ALREADY_EXISTS"));

        var request = new com.mediahub.iam.dto.RegisterRequest();
        request.setName("Test User");
        request.setEmail("test@mediahub.com");
        request.setPassword("Test@123");
        request.setPhone("+91-9111111111");
        request.setCountry("IN");

        assertThrows(RuntimeException.class,
            () -> authController.register(request));
    }

    @Test
    @DisplayName("TC-CTRL-AUTH-03: Login returns 200 with token on success")
    void testLoginSuccess() {
        Map<String, Object> loginResponse = new HashMap<>();
        loginResponse.put("accessToken", "eyJhbGciOiJIUzI1NiJ9.mock");
        loginResponse.put("tokenType", "Bearer");
        loginResponse.put("expiresIn", 1800);

        when(authService.login(anyString(), anyString()))
            .thenReturn(loginResponse);

        var request = new com.mediahub.iam.dto.LoginRequest();
        request.setEmail("test@mediahub.com");
        request.setPassword("Test@123");

        ResponseEntity<?> response = authController.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("TC-CTRL-AUTH-04: Login throws for invalid credentials")
    void testLoginInvalidCredentials() {
        when(authService.login(anyString(), anyString()))
            .thenThrow(new RuntimeException("INVALID_CREDENTIALS"));

        var request = new com.mediahub.iam.dto.LoginRequest();
        request.setEmail("test@mediahub.com");
        request.setPassword("WrongPass");

        assertThrows(RuntimeException.class,
            () -> authController.login(request));
    }

    @Test
    @DisplayName("TC-CTRL-AUTH-05: Logout returns 200 on success")
    void testLogoutSuccess() {
        doNothing().when(authService).logout(1L);

        ResponseEntity<?> response = authController.logout(1L);

        assertEquals(200, response.getStatusCode().value());
        verify(authService, times(1)).logout(1L);
    }
}
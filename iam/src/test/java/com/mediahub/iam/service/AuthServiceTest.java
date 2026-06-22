package com.mediahub.iam.service;

import com.mediahub.iam.entity.Role;
import com.mediahub.iam.entity.User;
import com.mediahub.iam.enums.UserStatus;
import com.mediahub.iam.repository.RoleRepository;
import com.mediahub.iam.repository.UserRepository;
import com.mediahub.iam.security.JwtUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuditLogService auditLogService;

    @InjectMocks private AuthService authService;

    private Role subscriberRole;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        subscriberRole = new Role();
        subscriberRole.setRoleId(1L);
        subscriberRole.setRoleType("subscriber");

        sampleUser = new User();
        sampleUser.setUserId(1L);
        sampleUser.setName("Test User");
        sampleUser.setEmail("test@mediahub.com");
        sampleUser.setPasswordHash("Pass@123");
        sampleUser.setRole(subscriberRole);
        sampleUser.setStatus(UserStatus.active);
        sampleUser.setIsRevoked(false);
        sampleUser.setCountry("IN");
    }

    // ── Register Tests ────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-AUTH-01: Register new user successfully")
    void testRegisterSuccess() {
        when(userRepository.findByEmail("test@mediahub.com"))
            .thenReturn(Optional.empty());
        when(roleRepository.findByRoleType("subscriber"))
            .thenReturn(Optional.of(subscriberRole));
        when(userRepository.save(any(User.class)))
            .thenReturn(sampleUser);

        User result = authService.register(
            "Test User", "test@mediahub.com",
            "Pass@123", "+91-9111111111", "IN");

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("test@mediahub.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("TC-AUTH-02: Register fails when email already exists")
    void testRegisterEmailAlreadyExists() {
        when(userRepository.findByEmail("test@mediahub.com"))
            .thenReturn(Optional.of(sampleUser));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            authService.register(
                "Test User", "test@mediahub.com",
                "Pass@123", "+91-9111111111", "IN"));

        assertEquals("EMAIL_ALREADY_EXISTS", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("TC-AUTH-03: Register fails when default role not found")
    void testRegisterDefaultRoleNotFound() {
        when(userRepository.findByEmail("new@mediahub.com"))
            .thenReturn(Optional.empty());
        when(roleRepository.findByRoleType("subscriber"))
            .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            authService.register(
                "New User", "new@mediahub.com",
                "Pass@123", "+91-9111111111", "IN"));
    }

    // ── Login Tests ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-AUTH-04: Login successfully returns JWT token")
    void testLoginSuccess() {
        when(userRepository.findByEmail("test@mediahub.com"))
            .thenReturn(Optional.of(sampleUser));
        when(jwtUtil.generateToken(sampleUser))
            .thenReturn("eyJhbGciOiJIUzI1NiJ9.mocktoken");
        when(userRepository.save(any(User.class)))
            .thenReturn(sampleUser);

        Map<String, Object> result = authService.login(
            "test@mediahub.com", "Pass@123");

        assertNotNull(result);
        assertTrue(result.containsKey("accessToken"));
        assertEquals("eyJhbGciOiJIUzI1NiJ9.mocktoken",
            result.get("accessToken"));
        assertEquals("Bearer", result.get("tokenType"));
    }

    @Test
    @DisplayName("TC-AUTH-05: Login fails with wrong password")
    void testLoginWrongPassword() {
        when(userRepository.findByEmail("test@mediahub.com"))
            .thenReturn(Optional.of(sampleUser));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            authService.login("test@mediahub.com", "WrongPass"));

        assertEquals("INVALID_CREDENTIALS", ex.getMessage());
    }

    @Test
    @DisplayName("TC-AUTH-06: Login fails for suspended user")
    void testLoginSuspendedUser() {
        sampleUser.setStatus(UserStatus.suspended);
        when(userRepository.findByEmail("test@mediahub.com"))
            .thenReturn(Optional.of(sampleUser));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            authService.login("test@mediahub.com", "Pass@123"));

        assertEquals("ACCOUNT_SUSPENDED", ex.getMessage());
    }

    @Test
    @DisplayName("TC-AUTH-07: Login fails for inactive user")
    void testLoginInactiveUser() {
        sampleUser.setStatus(UserStatus.inactive);
        when(userRepository.findByEmail("test@mediahub.com"))
            .thenReturn(Optional.of(sampleUser));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            authService.login("test@mediahub.com", "Pass@123"));

        assertEquals("ACCOUNT_INACTIVE", ex.getMessage());
    }

    @Test
    @DisplayName("TC-AUTH-08: Login fails when user not found")
    void testLoginUserNotFound() {
        when(userRepository.findByEmail("notfound@mediahub.com"))
            .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            authService.login("notfound@mediahub.com", "Pass@123"));

        assertEquals("INVALID_CREDENTIALS", ex.getMessage());
    }

    // ── Logout Tests ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-AUTH-09: Logout clears session token")
    void testLogoutSuccess() {
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class)))
            .thenReturn(sampleUser);

        authService.logout(1L);

        verify(userRepository, times(1)).save(any(User.class));
        assertTrue(sampleUser.getIsRevoked());
        assertNull(sampleUser.getTokenHash());
    }
}
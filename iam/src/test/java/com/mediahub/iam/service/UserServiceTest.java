package com.mediahub.iam.service;

import com.mediahub.iam.entity.Role;
import com.mediahub.iam.entity.User;
import com.mediahub.iam.enums.UserStatus;
import com.mediahub.iam.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private AuditLogService auditLogService;
    @InjectMocks private UserService userService;

    private User sampleUser;
    private Role subscriberRole;

    @BeforeEach
    void setUp() {
        subscriberRole = new Role();
        subscriberRole.setRoleId(1L);
        subscriberRole.setRoleType("subscriber");

        sampleUser = new User();
        sampleUser.setUserId(1L);
        sampleUser.setName("Arjun Sharma");
        sampleUser.setEmail("arjun@email.com");
        sampleUser.setRole(subscriberRole);
        sampleUser.setStatus(UserStatus.active);
        sampleUser.setIsRevoked(false);
        sampleUser.setCountry("IN");
        sampleUser.setPasswordHash("hash123");
    }

    // ── Get Tests ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-USER-01: Get all users returns list")
    void testGetAllUsers() {
        when(userRepository.findAll())
            .thenReturn(List.of(sampleUser));

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Arjun Sharma", result.get(0).getName());
    }

    @Test
    @DisplayName("TC-USER-02: Get user by ID returns correct user")
    void testGetUserById() {
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(sampleUser));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("Arjun Sharma", result.getName());
    }

    @Test
    @DisplayName("TC-USER-03: Get user by ID throws when not found")
    void testGetUserByIdNotFound() {
        when(userRepository.findById(99L))
            .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> userService.getUserById(99L));

        assertEquals("USER_NOT_FOUND", ex.getMessage());
    }

    // ── Update Tests ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-USER-04: Update user profile successfully")
    void testUpdateUser() {
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class)))
            .thenReturn(sampleUser);

        User result = userService.updateUser(
            1L, "Arjun Kumar", "+91-9888888888", "IN");

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ── Suspend Tests ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-USER-05: Suspend user successfully")
    void testSuspendUser() {
        User adminUser = new User();
        adminUser.setUserId(2L);
        adminUser.setName("Admin");

        when(userRepository.findById(1L))
            .thenReturn(Optional.of(sampleUser));
        when(userRepository.findById(2L))
            .thenReturn(Optional.of(adminUser));
        when(userRepository.save(any(User.class)))
            .thenReturn(sampleUser);

        User result = userService.suspendUser(
            1L, "Policy violation", 2L);

        assertNotNull(result);
        assertEquals(UserStatus.suspended, sampleUser.getStatus());
        assertTrue(sampleUser.getIsRevoked());
        assertNull(sampleUser.getTokenHash());
    }

    @Test
    @DisplayName("TC-USER-06: Suspend fails when user already suspended")
    void testSuspendAlreadySuspended() {
        sampleUser.setStatus(UserStatus.suspended);
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(sampleUser));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> userService.suspendUser(1L, "reason", 2L));

        assertEquals("ALREADY_SUSPENDED", ex.getMessage());
    }

    @Test
    @DisplayName("TC-USER-07: Suspend fails when admin suspends self")
    void testSuspendSelf() {
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(sampleUser));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> userService.suspendUser(1L, "reason", 1L));

        assertEquals("CANNOT_SUSPEND_SELF", ex.getMessage());
    }

    // ── Activate Tests ────────────────────────────────────────────────────────

    @Test
    @DisplayName("TC-USER-08: Activate suspended user successfully")
    void testActivateUser() {
        sampleUser.setStatus(UserStatus.suspended);
        sampleUser.setIsRevoked(true);

        when(userRepository.findById(1L))
            .thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class)))
            .thenReturn(sampleUser);

        User result = userService.activateUser(1L);

        assertNotNull(result);
        assertEquals(UserStatus.active, sampleUser.getStatus());
        assertFalse(sampleUser.getIsRevoked());
    }

    @Test
    @DisplayName("TC-USER-09: Activate fails when user already active")
    void testActivateAlreadyActive() {
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(sampleUser));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> userService.activateUser(1L));

        assertEquals("ALREADY_ACTIVE", ex.getMessage());
    }
}
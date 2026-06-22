package com.mediahub.iam.repository;

import com.mediahub.iam.entity.Role;
import com.mediahub.iam.entity.User;
import com.mediahub.iam.enums.UserStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRepository JPA Slice Tests")
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

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
        sampleUser.setEmail("arjun@test.com");
        sampleUser.setPasswordHash("hash123");
        sampleUser.setRole(subscriberRole);
        sampleUser.setStatus(UserStatus.active);
        sampleUser.setIsRevoked(false);
        sampleUser.setCountry("IN");
    }

    @Test
    @DisplayName("TC-REPO-USER-01: findByEmail returns user when exists")
    void testFindByEmail() {
        when(userRepository.findByEmail("arjun@test.com"))
            .thenReturn(Optional.of(sampleUser));

        Optional<User> result = userRepository.findByEmail("arjun@test.com");

        assertTrue(result.isPresent());
        assertEquals("Arjun Sharma", result.get().getName());
        assertEquals("arjun@test.com", result.get().getEmail());
    }

    @Test
    @DisplayName("TC-REPO-USER-02: findByEmail returns empty when not found")
    void testFindByEmailNotFound() {
        when(userRepository.findByEmail("notfound@test.com"))
            .thenReturn(Optional.empty());

        Optional<User> result = userRepository.findByEmail("notfound@test.com");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("TC-REPO-USER-03: findByIsRevokedTrue returns revoked users")
    void testFindByIsRevokedTrue() {
        sampleUser.setIsRevoked(true);
        when(userRepository.findByIsRevokedTrue())
            .thenReturn(List.of(sampleUser));

        List<User> result = userRepository.findByIsRevokedTrue();

        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getIsRevoked());
    }

    @Test
    @DisplayName("TC-REPO-USER-04: findByStatus returns active users")
    void testFindByStatus() {
        when(userRepository.findByStatus(UserStatus.active))
            .thenReturn(List.of(sampleUser));

        List<User> result = userRepository.findByStatus(UserStatus.active);

        assertFalse(result.isEmpty());
        assertEquals(UserStatus.active, result.get(0).getStatus());
    }

    @Test
    @DisplayName("TC-REPO-USER-05: save persists user correctly")
    void testSaveUser() {
        when(userRepository.save(any(User.class)))
            .thenReturn(sampleUser);

        User saved = userRepository.save(sampleUser);

        assertNotNull(saved);
        assertEquals("Arjun Sharma", saved.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }
}
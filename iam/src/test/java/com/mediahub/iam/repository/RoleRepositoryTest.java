package com.mediahub.iam.repository;

import com.mediahub.iam.entity.Role;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleRepository JPA Slice Tests")
class RoleRepositoryTest {

    @Mock
    private RoleRepository roleRepository;

    private Role sampleRole;

    @BeforeEach
    void setUp() {
        sampleRole = new Role();
        sampleRole.setRoleId(1L);
        sampleRole.setRoleType("subscriber");
    }

    @Test
    @DisplayName("TC-REPO-ROLE-01: findByRoleType returns correct role")
    void testFindByRoleType() {
        when(roleRepository.findByRoleType("subscriber"))
            .thenReturn(Optional.of(sampleRole));

        Optional<Role> result = roleRepository.findByRoleType("subscriber");

        assertTrue(result.isPresent());
        assertEquals("subscriber", result.get().getRoleType());
    }

    @Test
    @DisplayName("TC-REPO-ROLE-02: findByRoleType returns empty when not found")
    void testFindByRoleTypeNotFound() {
        when(roleRepository.findByRoleType("nonexistent"))
            .thenReturn(Optional.empty());

        Optional<Role> result = roleRepository.findByRoleType("nonexistent");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("TC-REPO-ROLE-03: save persists role correctly")
    void testSaveRole() {
        Role newRole = new Role();
        newRole.setRoleType("admin");

        when(roleRepository.save(any(Role.class))).thenReturn(newRole);

        Role saved = roleRepository.save(newRole);

        assertNotNull(saved);
        assertEquals("admin", saved.getRoleType());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    @DisplayName("TC-REPO-ROLE-04: findAll returns all roles")
    void testFindAll() {
        when(roleRepository.findAll()).thenReturn(List.of(sampleRole));

        List<Role> result = roleRepository.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}
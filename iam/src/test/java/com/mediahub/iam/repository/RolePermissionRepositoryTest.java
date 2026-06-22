package com.mediahub.iam.repository;

import com.mediahub.iam.entity.Permission;
import com.mediahub.iam.entity.Role;
import com.mediahub.iam.entity.RolePermission;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RolePermissionRepository JPA Slice Tests")
class RolePermissionRepositoryTest {

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    private Role sampleRole;
    private Permission samplePermission;
    private RolePermission sampleRolePermission;

    @BeforeEach
    void setUp() {
        sampleRole = new Role();
        sampleRole.setRoleId(1L);
        sampleRole.setRoleType("creator");

        samplePermission = new Permission();
        samplePermission.setPermissionId(1L);
        samplePermission.setPermissionType("content:write");

        sampleRolePermission = new RolePermission();
        sampleRolePermission.setRole(sampleRole);
        sampleRolePermission.setPermission(samplePermission);
    }

    @Test
    @DisplayName("TC-REPO-RP-01: findByRole returns correct list")
    void testFindByRole() {
        when(rolePermissionRepository.findByRole(sampleRole))
            .thenReturn(List.of(sampleRolePermission));

        List<RolePermission> result =
            rolePermissionRepository.findByRole(sampleRole);

        assertFalse(result.isEmpty());
        assertEquals("creator",
            result.get(0).getRole().getRoleType());
    }

    @Test
    @DisplayName("TC-REPO-RP-02: findByRoleAndPermission returns correct entry")
    void testFindByRoleAndPermission() {
        when(rolePermissionRepository
            .findByRoleAndPermission(sampleRole, samplePermission))
            .thenReturn(Optional.of(sampleRolePermission));

        Optional<RolePermission> result =
            rolePermissionRepository.findByRoleAndPermission(
                sampleRole, samplePermission);

        assertTrue(result.isPresent());
        assertEquals("content:write",
            result.get().getPermission().getPermissionType());
    }

    @Test
    @DisplayName("TC-REPO-RP-03: findByRoleAndPermission returns empty when not assigned")
    void testFindByRoleAndPermissionNotAssigned() {
        Permission otherPerm = new Permission();
        otherPerm.setPermissionId(2L);
        otherPerm.setPermissionType("audit:read");

        when(rolePermissionRepository
            .findByRoleAndPermission(sampleRole, otherPerm))
            .thenReturn(Optional.empty());

        Optional<RolePermission> result =
            rolePermissionRepository.findByRoleAndPermission(
                sampleRole, otherPerm);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("TC-REPO-RP-04: save persists role permission correctly")
    void testSave() {
        when(rolePermissionRepository.save(any(RolePermission.class)))
            .thenReturn(sampleRolePermission);

        RolePermission saved =
            rolePermissionRepository.save(sampleRolePermission);

        assertNotNull(saved);
        verify(rolePermissionRepository, times(1))
            .save(any(RolePermission.class));
    }
}
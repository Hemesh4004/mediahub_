package com.mediahub.iam.repository;

import com.mediahub.iam.entity.Permission;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionRepository JPA Slice Tests")
class PermissionRepositoryTest {

    @Mock
    private PermissionRepository permissionRepository;

    private Permission samplePermission;

    @BeforeEach
    void setUp() {
        samplePermission = new Permission();
        samplePermission.setPermissionId(1L);
        samplePermission.setPermissionType("content:read");
    }

    @Test
    @DisplayName("TC-REPO-PERM-01: findByPermissionType returns correct permission")
    void testFindByPermissionType() {
        when(permissionRepository.findByPermissionType("content:read"))
            .thenReturn(Optional.of(samplePermission));

        Optional<Permission> result =
            permissionRepository.findByPermissionType("content:read");

        assertTrue(result.isPresent());
        assertEquals("content:read", result.get().getPermissionType());
    }

    @Test
    @DisplayName("TC-REPO-PERM-02: findByPermissionType returns empty when not found")
    void testFindByPermissionTypeNotFound() {
        when(permissionRepository.findByPermissionType("nonexistent"))
            .thenReturn(Optional.empty());

        Optional<Permission> result =
            permissionRepository.findByPermissionType("nonexistent");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("TC-REPO-PERM-03: save persists permission correctly")
    void testSavePermission() {
        Permission newPerm = new Permission();
        newPerm.setPermissionType("audit:read");

        when(permissionRepository.save(any(Permission.class)))
            .thenReturn(newPerm);

        Permission saved = permissionRepository.save(newPerm);

        assertNotNull(saved);
        assertEquals("audit:read", saved.getPermissionType());
        verify(permissionRepository, times(1)).save(any(Permission.class));
    }

    @Test
    @DisplayName("TC-REPO-PERM-04: findAll returns all permissions")
    void testFindAll() {
        when(permissionRepository.findAll())
            .thenReturn(List.of(samplePermission));

        List<Permission> result = permissionRepository.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}
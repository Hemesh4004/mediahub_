package com.mediahub.iam.service;

import com.mediahub.iam.entity.Permission;
import com.mediahub.iam.repository.PermissionRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionService Unit Tests")
class PermissionServiceTest {

    @Mock private PermissionRepository permissionRepository;
    @InjectMocks private PermissionService permissionService;

    private Permission samplePermission;

    @BeforeEach
    void setUp() {
        samplePermission = new Permission();
        samplePermission.setPermissionId(1L);
        samplePermission.setPermissionType("content:read");
    }

    @Test
    @DisplayName("TC-PERM-01: Get all permissions returns list")
    void testGetAllPermissions() {
        when(permissionRepository.findAll())
            .thenReturn(List.of(samplePermission));
        List<Permission> result = permissionService.getAllPermissions();
        assertEquals(1, result.size());
        assertEquals("content:read", result.get(0).getPermissionType());
    }

    @Test
    @DisplayName("TC-PERM-02: Get permission by ID returns correct permission")
    void testGetPermissionById() {
        when(permissionRepository.findById(1L))
            .thenReturn(Optional.of(samplePermission));
        Permission result = permissionService.getPermissionById(1L);
        assertNotNull(result);
        assertEquals("content:read", result.getPermissionType());
    }

    @Test
    @DisplayName("TC-PERM-03: Get permission by ID throws when not found")
    void testGetPermissionByIdNotFound() {
        when(permissionRepository.findById(99L))
            .thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> permissionService.getPermissionById(99L));
        assertEquals("PERMISSION_NOT_FOUND", ex.getMessage());
    }

    @Test
    @DisplayName("TC-PERM-04: Create permission successfully")
    void testCreatePermission() {
        when(permissionRepository.findByPermissionType("analytics:export"))
            .thenReturn(Optional.empty());
        when(permissionRepository.save(any(Permission.class)))
            .thenReturn(samplePermission);
        Permission result = permissionService.createPermission("analytics:export");
        assertNotNull(result);
        verify(permissionRepository, times(1)).save(any(Permission.class));
    }

    @Test
    @DisplayName("TC-PERM-05: Create permission fails when already exists")
    void testCreatePermissionDuplicate() {
        when(permissionRepository.findByPermissionType("content:read"))
            .thenReturn(Optional.of(samplePermission));
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> permissionService.createPermission("content:read"));
        assertEquals("PERMISSION_ALREADY_EXISTS", ex.getMessage());
    }

    @Test
    @DisplayName("TC-PERM-06: Update permission successfully")
    void testUpdatePermission() {
        when(permissionRepository.findById(1L))
            .thenReturn(Optional.of(samplePermission));
        when(permissionRepository.save(any(Permission.class)))
            .thenReturn(samplePermission);
        Permission result = permissionService.updatePermission(
            1L, "content:view");
        assertNotNull(result);
        verify(permissionRepository, times(1)).save(any(Permission.class));
    }

    @Test
    @DisplayName("TC-PERM-07: Delete permission successfully")
    void testDeletePermission() {
        doNothing().when(permissionRepository).deleteById(1L);
        permissionService.deletePermission(1L);
        verify(permissionRepository, times(1)).deleteById(1L);
    }
}
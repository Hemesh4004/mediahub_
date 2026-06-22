package com.mediahub.iam.service;

import com.mediahub.iam.entity.Permission;
import com.mediahub.iam.entity.Role;
import com.mediahub.iam.entity.RolePermission;
import com.mediahub.iam.repository.PermissionRepository;
import com.mediahub.iam.repository.RolePermissionRepository;
import com.mediahub.iam.repository.RoleRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleService Unit Tests")
class RoleServiceTest {

    @Mock private RoleRepository roleRepository;
    @Mock private PermissionRepository permissionRepository;
    @Mock private RolePermissionRepository rolePermissionRepository;
    @InjectMocks private RoleService roleService;

    private Role sampleRole;
    private Permission samplePermission;

    @BeforeEach
    void setUp() {
        sampleRole = new Role();
        sampleRole.setRoleId(1L);
        sampleRole.setRoleType("subscriber");

        samplePermission = new Permission();
        samplePermission.setPermissionId(1L);
        samplePermission.setPermissionType("content:read");
    }

    @Test
    @DisplayName("TC-ROLE-01: Get all roles returns list")
    void testGetAllRoles() {
        when(roleRepository.findAll()).thenReturn(List.of(sampleRole));
        List<Role> result = roleService.getAllRoles();
        assertEquals(1, result.size());
        assertEquals("subscriber", result.get(0).getRoleType());
    }

    @Test
    @DisplayName("TC-ROLE-02: Get role by ID returns correct role")
    void testGetRoleById() {
        when(roleRepository.findById(1L))
            .thenReturn(Optional.of(sampleRole));
        Role result = roleService.getRoleById(1L);
        assertNotNull(result);
        assertEquals("subscriber", result.getRoleType());
    }

    @Test
    @DisplayName("TC-ROLE-03: Get role by ID throws when not found")
    void testGetRoleByIdNotFound() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> roleService.getRoleById(99L));
        assertEquals("ROLE_NOT_FOUND", ex.getMessage());
    }

    @Test
    @DisplayName("TC-ROLE-04: Create role successfully")
    void testCreateRole() {
        when(roleRepository.findByRoleType("contentModerator"))
            .thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(sampleRole);
        Role result = roleService.createRole("contentModerator");
        assertNotNull(result);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    @DisplayName("TC-ROLE-05: Create role fails when role already exists")
    void testCreateRoleDuplicate() {
        when(roleRepository.findByRoleType("subscriber"))
            .thenReturn(Optional.of(sampleRole));
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> roleService.createRole("subscriber"));
        assertEquals("ROLE_ALREADY_EXISTS", ex.getMessage());
    }

    @Test
    @DisplayName("TC-ROLE-06: Update role successfully")
    void testUpdateRole() {
        when(roleRepository.findById(1L))
            .thenReturn(Optional.of(sampleRole));
        when(roleRepository.save(any(Role.class))).thenReturn(sampleRole);
        Role result = roleService.updateRole(1L, "contentReviewer");
        assertNotNull(result);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    @DisplayName("TC-ROLE-07: Delete role successfully")
    void testDeleteRole() {
        doNothing().when(roleRepository).deleteById(1L);
        roleService.deleteRole(1L);
        verify(roleRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("TC-ROLE-08: Assign permission to role successfully")
    void testAssignPermission() {
        when(roleRepository.findById(1L))
            .thenReturn(Optional.of(sampleRole));
        when(permissionRepository.findById(1L))
            .thenReturn(Optional.of(samplePermission));
        when(rolePermissionRepository
            .findByRoleAndPermission(sampleRole, samplePermission))
            .thenReturn(Optional.empty());
        when(rolePermissionRepository.save(any(RolePermission.class)))
            .thenReturn(new RolePermission());

        RolePermission result = roleService.assignPermission(1L, 1L);
        assertNotNull(result);
        verify(rolePermissionRepository, times(1))
            .save(any(RolePermission.class));
    }

    @Test
    @DisplayName("TC-ROLE-09: Assign permission fails when already assigned")
    void testAssignPermissionAlreadyAssigned() {
        when(roleRepository.findById(1L))
            .thenReturn(Optional.of(sampleRole));
        when(permissionRepository.findById(1L))
            .thenReturn(Optional.of(samplePermission));
        when(rolePermissionRepository
            .findByRoleAndPermission(sampleRole, samplePermission))
            .thenReturn(Optional.of(new RolePermission()));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> roleService.assignPermission(1L, 1L));
        assertEquals("PERMISSION_ALREADY_ASSIGNED", ex.getMessage());
    }
}
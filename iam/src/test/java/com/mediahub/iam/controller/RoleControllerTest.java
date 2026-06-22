package com.mediahub.iam.controller;

import com.mediahub.iam.dto.AssignPermissionRequest;
import com.mediahub.iam.entity.Role;
import com.mediahub.iam.entity.RolePermission;
import com.mediahub.iam.service.RoleService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleController Web Slice Tests")
class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private Role sampleRole;

    @BeforeEach
    void setUp() {
        sampleRole = new Role();
        sampleRole.setRoleId(1L);
        sampleRole.setRoleType("subscriber");
    }

    @Test
    @DisplayName("TC-CTRL-ROLE-01: getAllRoles returns 200 with role list")
    void testGetAllRoles() {
        when(roleService.getAllRoles()).thenReturn(List.of(sampleRole));

        ResponseEntity<Map<String, Object>> response =
            roleController.getAllRoles();

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Roles retrieved", response.getBody().get("message"));
    }

    @Test
    @DisplayName("TC-CTRL-ROLE-02: getRole returns 200 for valid roleId")
    void testGetRoleById() {
        when(roleService.getRoleById(1L)).thenReturn(sampleRole);

        ResponseEntity<Map<String, Object>> response =
            roleController.getRole(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Role retrieved", response.getBody().get("message"));
    }

    @Test
    @DisplayName("TC-CTRL-ROLE-03: createRole returns 201 on success")
    void testCreateRole() {
        when(roleService.createRole(anyString())).thenReturn(sampleRole);

        Map<String, String> body = Map.of("roleType", "contentModerator");

        ResponseEntity<Map<String, String>> response =
            roleController.createRole(body);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("Role created successfully",
            response.getBody().get("message"));
    }

    @Test
    @DisplayName("TC-CTRL-ROLE-04: updateRole returns 200 on success")
    void testUpdateRole() {
        when(roleService.updateRole(anyLong(), anyString()))
            .thenReturn(sampleRole);

        Map<String, String> body = Map.of("roleType", "contentReviewer");

        ResponseEntity<Map<String, String>> response =
            roleController.updateRole(1L, body);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Role updated successfully",
            response.getBody().get("message"));
    }

    @Test
    @DisplayName("TC-CTRL-ROLE-05: assignPermission returns 201 on success")
    void testAssignPermission() {
        when(roleService.assignPermission(anyLong(), anyLong()))
            .thenReturn(new RolePermission());

        AssignPermissionRequest request = new AssignPermissionRequest();
        request.setPermissionId(1L);

        ResponseEntity<Map<String, String>> response =
            roleController.assignPermission(1L, request);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("Permission assigned successfully",
            response.getBody().get("message"));
    }
}
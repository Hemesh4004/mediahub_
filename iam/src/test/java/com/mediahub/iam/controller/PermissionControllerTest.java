package com.mediahub.iam.controller;

import com.mediahub.iam.entity.Permission;
import com.mediahub.iam.service.PermissionService;
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
@DisplayName("PermissionController Web Slice Tests")
class PermissionControllerTest {

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private PermissionController permissionController;

    private Permission samplePermission;

    @BeforeEach
    void setUp() {
        samplePermission = new Permission();
        samplePermission.setPermissionId(1L);
        samplePermission.setPermissionType("content:read");
    }

    @Test
    @DisplayName("TC-CTRL-PERM-01: getAllPermissions returns 200 with list")
    void testGetAllPermissions() {
        when(permissionService.getAllPermissions())
            .thenReturn(List.of(samplePermission));

        ResponseEntity<Map<String, Object>> response =
            permissionController.getAllPermissions();

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Permissions retrieved",
            response.getBody().get("message"));
    }

    @Test
    @DisplayName("TC-CTRL-PERM-02: getPermission returns 200 for valid id")
    void testGetPermissionById() {
        when(permissionService.getPermissionById(1L))
            .thenReturn(samplePermission);

        ResponseEntity<Map<String, Object>> response =
            permissionController.getPermission(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Permission retrieved",
            response.getBody().get("message"));
    }

    @Test
    @DisplayName("TC-CTRL-PERM-03: createPermission returns 201 on success")
    void testCreatePermission() {
        when(permissionService.createPermission(anyString()))
            .thenReturn(samplePermission);

        Map<String, String> body = Map.of("permissionType", "analytics:export");

        ResponseEntity<Map<String, String>> response =
            permissionController.createPermission(body);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("Permission created successfully",
            response.getBody().get("message"));
    }

    @Test
    @DisplayName("TC-CTRL-PERM-04: updatePermission returns 200 on success")
    void testUpdatePermission() {
        when(permissionService.updatePermission(anyLong(), anyString()))
            .thenReturn(samplePermission);

        Map<String, String> body =
            Map.of("permissionType", "analytics:exportCsv");

        ResponseEntity<Map<String, String>> response =
            permissionController.updatePermission(1L, body);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Permission updated successfully",
            response.getBody().get("message"));
    }
}
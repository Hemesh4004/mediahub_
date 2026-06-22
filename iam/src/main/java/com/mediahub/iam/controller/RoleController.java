package com.mediahub.iam.controller;

import com.mediahub.iam.dto.AssignPermissionRequest;
import com.mediahub.iam.entity.Permission;
import com.mediahub.iam.entity.Role;
import com.mediahub.iam.repository.RolePermissionRepository;
import com.mediahub.iam.service.PermissionService;
import com.mediahub.iam.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping("/mediaHub/iam/roles")
public class RoleController {

    
    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;

    
    @GetMapping("/getAllRoles/v1.0")
    public ResponseEntity<Map<String, Object>> getAllRoles() {
        return ResponseEntity.ok(
            Map.of("message", "Roles retrieved",
                   "data", roleService.getAllRoles()));
    }

    
    @GetMapping("/getRole/v1/{roleId}")
    public ResponseEntity<Map<String, Object>> getRole(
            @PathVariable Long roleId) {
        return ResponseEntity.ok(
            Map.of("message", "Role retrieved",
                   "data", roleService.getRoleById(roleId)));
    }

    
    @GetMapping("/getPermissions/v1/{roleId}")
    public ResponseEntity<Map<String, Object>> getPermissions(
            @PathVariable Long roleId) {
        return ResponseEntity.ok(
            Map.of("message", "Permissions retrieved",
                   "roleId", roleId,
                   "permissions", roleService.getPermissionsForRole(roleId)));
    }

    
    @PostMapping("/createRole/v1.0")
    public ResponseEntity<Map<String, String>> createRole(
            @RequestBody Map<String, String> body) {
        roleService.createRole(body.get("roleType"));
        return ResponseEntity.status(201).body(
            Map.of("message", "Role created successfully"));
    }

    
    @PutMapping("/updateRole/v1/{roleId}")
    public ResponseEntity<Map<String, String>> updateRole(
            @PathVariable Long roleId,
            @RequestBody Map<String, String> body) {
        roleService.updateRole(roleId, body.get("roleType"));
        return ResponseEntity.ok(
            Map.of("message", "Role updated successfully"));
    }

    
    @DeleteMapping("/revokePermission/v1/{roleId}/{permissionId}")
    public ResponseEntity<Map<String, String>> revokePermission(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {

        Role role = roleService.getRoleById(roleId);
        Permission permission = permissionService.getPermissionById(permissionId);

        rolePermissionRepository.findByRoleAndPermission(role, permission)
            .orElseThrow(() -> new RuntimeException("PERMISSION_NOT_ASSIGNED"));

        rolePermissionRepository.deleteByRoleAndPermission(role, permission);

        return ResponseEntity.ok(
            Map.of("message", "Permission revoked successfully"));
    }

    
    @PostMapping("/assignPermission/v1/{roleId}")
    public ResponseEntity<Map<String, String>> assignPermission(
            @PathVariable Long roleId,
            @RequestBody AssignPermissionRequest request) {
        roleService.assignPermission(roleId, request.getPermissionId());
        return ResponseEntity.status(201).body(
            Map.of("message", "Permission assigned successfully"));
    }
}



























// REST controller managing roles and their permission assignments.
// All routes are rooted at "/mediaHub/iam/roles" and return JSON wrapped in a status map.

// Spring-injected dependencies: the role-permission join repository plus the role and
    // permission services. These supply the data-access and business logic this controller delegates to.


// GET all roles
    // Handles GET "/getAllRoles/v1.0" and fetches every role record from the service.
    // Returns HTTP 200 with a message and the full role list under "data".

// GET single role
    // Handles GET "/getRole/v1/{roleId}", reading the id from the URL path.
    // Looks up that one role via the service and returns it as HTTP 200 with a message.

// GET permissions for role
    // Handles GET "/getPermissions/v1/{roleId}", taking the role id from the URL path.
    // Returns HTTP 200 with the list of permissions tied to that role, echoing the roleId back.

// POST create role
    // Handles POST "/createRole/v1.0", reading the "roleType" from the JSON body.
    // Asks the service to create the new role and returns HTTP 201 (Created) on success.

// PUT update role
    // Handles PUT "/updateRole/v1/{roleId}", taking the id from the path and the new type from the body.
    // Updates the matching role via the service and returns HTTP 200 with a confirmation message.

// DELETE revoke permission from role
    // Handles DELETE "/revokePermission/v1/{roleId}/{permissionId}", resolving both entities by id.
    // Verifies the permission is actually assigned (else throws), then deletes the role-permission link.
    // Returns HTTP 200 with a confirmation message once the mapping is removed.

// POST assign permission
    // Handles POST "/assignPermission/v1/{roleId}", reading the role id from the path and the
    // permission id from the AssignPermissionRequest body. Links the permission to the role via the
    // service and returns HTTP 201 (Created) on success.


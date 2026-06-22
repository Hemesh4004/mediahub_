package com.mediahub.iam.controller;

import com.mediahub.iam.dto.ActivateRequest;
import com.mediahub.iam.dto.SuspendRequest;
import com.mediahub.iam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping("/mediaHub/iam/users")
public class UserController {


    @Autowired
    private UserService userService;

    
    @GetMapping("/getAllUsers/v1.0")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        return ResponseEntity.ok(
            Map.of("message", "Users retrieved",
                   "data", userService.getAllUsers()));
    }

    
    @GetMapping("/getUser/v1/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
            Map.of("message", "User retrieved",
                   "data", userService.getUserById(userId)));
    }

    
    @PutMapping("/updateUser/v1/{userId}")
    public ResponseEntity<Map<String, String>> updateUser(
            @PathVariable Long userId,
            @RequestBody Map<String, String> body) {
        userService.updateUser(userId,
            body.get("name"),
            body.get("phone"),
            body.get("country"));
        return ResponseEntity.ok(
            Map.of("message", "User updated successfully"));
    }

    
    @DeleteMapping("/deleteUser/v1/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable Long userId,
            @RequestBody SuspendRequest request) {
        userService.softDeleteUser(userId, request.getReason());
        return ResponseEntity.ok(
            Map.of("message", "User deactivated successfully"));
    }

    
    @PostMapping("/suspendUser/v1/{userId}")
    public ResponseEntity<Map<String, String>> suspendUser(
            @PathVariable Long userId,
            @RequestBody SuspendRequest request) {
        userService.suspendUser(userId, request.getReason(), 1L);
        return ResponseEntity.ok(
            Map.of("message", "User suspended successfully"));
    }

    
    @PostMapping("/activateUser/v1/{userId}")
    public ResponseEntity<Map<String, String>> activateUser(
            @PathVariable Long userId,
            @RequestBody ActivateRequest request) {
        userService.activateUser(userId);
        return ResponseEntity.ok(
            Map.of("message", "User activated successfully"));
    }
}





















//Work flow of Controller.
// 1.Recieve the HTTP Request
// 2.Call the Service
// 3.Return the Response as JSON

// REST controller managing user accounts and their lifecycle (read, update, deactivate, suspend, activate).
// All routes are rooted at "/mediaHub/iam/users" and return JSON wrapped in a status map.

    // Service dependency holding the user business/data-access logic.
    // Spring injects the UserService bean automatically at startup via field injection.

// GET all users
    // Handles GET "/getAllUsers/v1.0" and fetches every user record from the service.
    // Returns HTTP 200 with a message and the full user list under "data".

// GET single user
    // Handles GET "/getUser/v1/{userId}", reading the id from the URL path.
    // Looks up that one user via the service and returns it as HTTP 200 with a message.

// PUT update user
    // Handles PUT "/updateUser/v1/{userId}", taking the id from the path and name/phone/country from the body.
    // Applies the profile changes via the service and returns HTTP 200 with a confirmation message.


// DELETE soft delete
    // Handles DELETE "/deleteUser/v1/{userId}", reading the id from the path and a reason from the body.
    // Performs a soft delete (deactivation, not physical removal) via the service and returns HTTP 200.


// POST suspend user
    // Handles POST "/suspendUser/v1/{userId}", taking the id from the path and a reason from the body.
    // Suspends the user via the service (the hardcoded 1L is the acting admin/actor id) and returns HTTP 200.

// POST activate user
    // Handles POST "/activateUser/v1/{userId}", reading the id from the URL path.
    // Re-activates a suspended/deactivated user via the service and returns HTTP 200 with a message.
package com.mediahub.iam.controller;

import com.mediahub.iam.dto.SuspendRequest;
import com.mediahub.iam.dto.ActivateRequest;
import com.mediahub.iam.entity.Role;
import com.mediahub.iam.entity.User;
import com.mediahub.iam.enums.UserStatus;
import com.mediahub.iam.service.UserService;
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
@DisplayName("UserController Web Slice Tests")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleId(1L);
        role.setRoleType("subscriber");

        sampleUser = new User();
        sampleUser.setUserId(1L);
        sampleUser.setName("Arjun Sharma");
        sampleUser.setEmail("arjun@email.com");
        sampleUser.setRole(role);
        sampleUser.setStatus(UserStatus.active);
        sampleUser.setIsRevoked(false);
        sampleUser.setCountry("IN");
    }

    @Test
    @DisplayName("TC-CTRL-USER-01: getAllUsers returns 200 with user list")
    void testGetAllUsers() {
        when(userService.getAllUsers()).thenReturn(List.of(sampleUser));

        ResponseEntity<Map<String, Object>> response =
            userController.getAllUsers();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Users retrieved", response.getBody().get("message"));
    }

    @Test
    @DisplayName("TC-CTRL-USER-02: getUser returns 200 for valid userId")
    void testGetUserById() {
        when(userService.getUserById(1L)).thenReturn(sampleUser);

        ResponseEntity<Map<String, Object>> response =
            userController.getUser(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("User retrieved", response.getBody().get("message"));
    }

    @Test
    @DisplayName("TC-CTRL-USER-03: getUser throws for invalid userId")
    void testGetUserByIdNotFound() {
        when(userService.getUserById(99L))
            .thenThrow(new RuntimeException("USER_NOT_FOUND"));

        assertThrows(RuntimeException.class,
            () -> userController.getUser(99L));
    }

    @Test
    @DisplayName("TC-CTRL-USER-04: updateUser returns 200 on success")
    void testUpdateUser() {
        when(userService.updateUser(anyLong(), anyString(),
            anyString(), anyString()))
            .thenReturn(sampleUser);

        Map<String, String> body = Map.of(
            "name", "Arjun Kumar",
            "phone", "+91-9888888888",
            "country", "IN"
        );

        ResponseEntity<Map<String, String>> response =
            userController.updateUser(1L, body);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("User updated successfully",
            response.getBody().get("message"));
    }

    @Test
    @DisplayName("TC-CTRL-USER-05: suspendUser returns 200 on success")
    void testSuspendUser() {
        sampleUser.setStatus(UserStatus.suspended);
        when(userService.suspendUser(anyLong(), anyString(), anyLong()))
            .thenReturn(sampleUser);

        SuspendRequest request = new SuspendRequest();
        request.setReason("Policy violation");

        ResponseEntity<Map<String, String>> response =
            userController.suspendUser(1L, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("User suspended successfully",
            response.getBody().get("message"));
    }
}
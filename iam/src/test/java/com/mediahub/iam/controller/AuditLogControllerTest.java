package com.mediahub.iam.controller;

import com.mediahub.iam.entity.AuditLog;
import com.mediahub.iam.entity.User;
import com.mediahub.iam.enums.AuditAction;
import com.mediahub.iam.enums.UserStatus;
import com.mediahub.iam.service.AuditLogService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditLogController Web Slice Tests")
class AuditLogControllerTest {

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuditLogController auditLogController;

    private AuditLog sampleLog;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUserId(1L);
        user.setName("System Admin");
        user.setStatus(UserStatus.active);
        user.setIsRevoked(false);

        sampleLog = new AuditLog();
        sampleLog.setAuditId(1L);
        sampleLog.setUser(user);
        sampleLog.setAction(AuditAction.created);
        sampleLog.setEntityType("user");
        sampleLog.setEntityId("2");
    }

    @Test
    @DisplayName("TC-CTRL-AUDIT-01: getAllLogs returns 200 with log list")
    void testGetAllLogs() {
        when(auditLogService.getAllLogs()).thenReturn(List.of(sampleLog));

        ResponseEntity<Map<String, Object>> response =
            auditLogController.getAllLogs();

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Audit logs retrieved",
            response.getBody().get("message"));
    }

    @Test
    @DisplayName("TC-CTRL-AUDIT-02: getLog returns 200 for valid auditId")
    void testGetLogById() {
        when(auditLogService.getLogById(1L)).thenReturn(sampleLog);

        ResponseEntity<Map<String, Object>> response =
            auditLogController.getLog(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Audit log retrieved",
            response.getBody().get("message"));
    }

    @Test
    @DisplayName("TC-CTRL-AUDIT-03: getUserLogs returns 200 for valid userId")
    void testGetUserLogs() {
        when(auditLogService.getLogsByUserId(1L))
            .thenReturn(List.of(sampleLog));

        ResponseEntity<Map<String, Object>> response =
            auditLogController.getUserLogs(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("User audit logs retrieved",
            response.getBody().get("message"));
    }
}
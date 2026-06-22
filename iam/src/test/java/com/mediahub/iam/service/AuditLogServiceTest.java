package com.mediahub.iam.service;

import com.mediahub.iam.entity.AuditLog;
import com.mediahub.iam.entity.User;
import com.mediahub.iam.enums.AuditAction;
import com.mediahub.iam.enums.UserStatus;
import com.mediahub.iam.repository.AuditLogRepository;
import com.mediahub.iam.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditLogService Unit Tests")
class AuditLogServiceTest {

    @Mock private AuditLogRepository auditLogRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private AuditLogService auditLogService;

    private AuditLog sampleLog;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setUserId(1L);
        sampleUser.setName("System Admin");
        sampleUser.setStatus(UserStatus.active);
        sampleUser.setIsRevoked(false);

        sampleLog = new AuditLog();
        sampleLog.setAuditId(1L);
        sampleLog.setUser(sampleUser);
        sampleLog.setAction(AuditAction.created);
        sampleLog.setEntityType("user");
        sampleLog.setEntityId("2");
    }

    @Test
    @DisplayName("TC-AUDIT-01: Get all logs returns list")
    void testGetAllLogs() {
        when(auditLogRepository.findAll())
            .thenReturn(List.of(sampleLog));
        List<AuditLog> result = auditLogService.getAllLogs();
        assertEquals(1, result.size());
        assertEquals(AuditAction.created, result.get(0).getAction());
    }

    @Test
    @DisplayName("TC-AUDIT-02: Get log by ID returns correct log")
    void testGetLogById() {
        when(auditLogRepository.findById(1L))
            .thenReturn(Optional.of(sampleLog));
        AuditLog result = auditLogService.getLogById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getAuditId());
    }

    @Test
    @DisplayName("TC-AUDIT-03: Get log by ID throws when not found")
    void testGetLogByIdNotFound() {
        when(auditLogRepository.findById(99L))
            .thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> auditLogService.getLogById(99L));
        assertEquals("AUDIT_LOG_NOT_FOUND", ex.getMessage());
    }

    @Test
    @DisplayName("TC-AUDIT-04: Get logs by userId returns correct list")
    void testGetLogsByUserId() {
        when(auditLogRepository.findByEntityId("2"))
            .thenReturn(List.of(sampleLog));
        List<AuditLog> result = auditLogService.getLogsByUserId(2L);
        assertEquals(1, result.size());
        assertEquals("2", result.get(0).getEntityId());
    }

    @Test
    @DisplayName("TC-AUDIT-05: Log action saves audit entry")
    void testLogAction() {
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(sampleUser));
        when(auditLogRepository.save(any(AuditLog.class)))
            .thenReturn(sampleLog);

        auditLogService.log(1L, AuditAction.created, "user",
            "2", null,
            "{\"name\":\"Test\"}", "127.0.0.1");

        verify(auditLogRepository, times(1))
            .save(any(AuditLog.class));
    }

    @Test
    @DisplayName("TC-AUDIT-06: Log action throws when user not found")
    void testLogActionUserNotFound() {
        when(userRepository.findById(99L))
            .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            auditLogService.log(99L, AuditAction.created,
                "user", "2", null, null, "127.0.0.1"));
    }
}
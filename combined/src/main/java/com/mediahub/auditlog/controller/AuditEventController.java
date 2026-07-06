package com.mediahub.auditlog.controller;

import com.mediahub.auditlog.dto.PageResponse;
import com.mediahub.auditlog.entity.AuditEvent;
import com.mediahub.auditlog.service.AuditEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/mediaHub/auditlog/events")
public class AuditEventController {

    @Autowired private AuditEventService auditEventService;

    // GET all events — paginated
    @GetMapping("/getAllEvents/v1.0")
    public ResponseEntity<Map<String, Object>> getAllEvents(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<AuditEvent> result =
            auditEventService.getAllEvents(page, size);
        return ResponseEntity.ok(Map.of(
            "message",       "Events retrieved",
            "data",          result.getData(),
            "currentPage",   result.getCurrentPage(),
            "totalPages",    result.getTotalPages(),
            "totalElements", result.getTotalElements(),
            "pageSize",      result.getPageSize()));
    }

    // GET single event
    @GetMapping("/getEvent/v1/{eventId}")
    public ResponseEntity<Map<String, Object>> getEvent(
            @PathVariable Long eventId) {
        return ResponseEntity.ok(Map.of(
            "message", "Event retrieved",
            "data",    auditEventService.getEventById(eventId)));
    }

    // GET events by user — paginated
    @GetMapping("/getByUser/v1/{userId}")
    public ResponseEntity<Map<String, Object>> getByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<AuditEvent> result =
            auditEventService.getEventsByUser(userId, page, size);
        return ResponseEntity.ok(Map.of(
            "message",       "User events retrieved",
            "data",          result.getData(),
            "currentPage",   result.getCurrentPage(),
            "totalPages",    result.getTotalPages(),
            "totalElements", result.getTotalElements()));
    }

    // GET events by module — paginated
    @GetMapping("/getByModule/v1/{moduleSource}")
    public ResponseEntity<Map<String, Object>> getByModule(
            @PathVariable AuditEvent.ModuleSource moduleSource,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<AuditEvent> result =
            auditEventService.getEventsByModule(moduleSource, page, size);
        return ResponseEntity.ok(Map.of(
            "message",       "Module events retrieved",
            "data",          result.getData(),
            "currentPage",   result.getCurrentPage(),
            "totalPages",    result.getTotalPages(),
            "totalElements", result.getTotalElements()));
    }

    // GET events by severity — paginated
    @GetMapping("/getBySeverity/v1/{severity}")
    public ResponseEntity<Map<String, Object>> getBySeverity(
            @PathVariable AuditEvent.Severity severity,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<AuditEvent> result =
            auditEventService.getEventsBySeverity(severity, page, size);
        return ResponseEntity.ok(Map.of(
            "message",       "Events retrieved",
            "data",          result.getData(),
            "currentPage",   result.getCurrentPage(),
            "totalPages",    result.getTotalPages(),
            "totalElements", result.getTotalElements()));
    }

    // GET events by entity
    @GetMapping("/getByEntity/v1/{entityType}/{entityId}")
    public ResponseEntity<Map<String, Object>> getByEntity(
            @PathVariable String entityType,
            @PathVariable String entityId) {
        return ResponseEntity.ok(Map.of(
            "message", "Entity events retrieved",
            "data",    auditEventService.getEventsByEntity(
                entityType, entityId)));
    }

    // POST log event
    @PostMapping("/logEvent/v1.0")
    public ResponseEntity<Map<String, Object>> logEvent(
            @RequestBody Map<String, Object> body) {
        AuditEvent event = auditEventService.logEvent(
            (String) body.get("eventType"),
            AuditEvent.ModuleSource.valueOf(
                (String) body.get("moduleSource")),
            body.get("performedBy") != null ?
                Long.valueOf(body.get("performedBy").toString()) : null,
            (String) body.get("performedByRole"),
            (String) body.get("targetEntityType"),
            (String) body.get("targetEntityId"),
            (String) body.get("oldValue"),
            (String) body.get("newValue"),
            (String) body.get("ipAddress"),
            AuditEvent.Severity.valueOf(
                body.getOrDefault("severity", "LOW").toString()),
            AuditEvent.EventStatus.valueOf(
                body.getOrDefault("status", "SUCCESS").toString()),
            (String) body.get("description"));
        return ResponseEntity.status(201).body(Map.of(
            "message", "Event logged successfully",
            "eventId", event.getEventId()));
    }
}
package com.mediahub.iam.repository;

import com.mediahub.iam.entity.AuditLog;
import com.mediahub.iam.entity.User;
import com.mediahub.iam.enums.AuditAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUser(User user);

    List<AuditLog> findByEntityId(String entityId);

    
    List<AuditLog> findByAction(AuditAction action);
}

















// Spring Data JPA repository for AuditLog entities (primary key type Long).
// Extending JpaRepository provides ready-made CRUD/paging methods; Spring auto-implements this interface at runtime.

// Derived query: returns all audit logs belonging to the given user.
// Spring generates the SQL automatically from the method name ("findBy" + field "User").

// Derived query: returns all audit logs referencing the given affected entity id.

// Derived query: returns all audit logs that recorded the given action type.
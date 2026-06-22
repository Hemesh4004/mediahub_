package com.mediahub.iam.repository;

import com.mediahub.iam.entity.User;
import com.mediahub.iam.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);

    List<User> findByIsRevokedTrue();

    List<User> findByStatus(UserStatus status);
}




















// Repository layer communicates with database by performing database operations, provides CRUD functionality.
// -Supports Custom query methods.

// UserRepository extends JpaRepository.
//- It tells Spring Data JPA : "Create an implementation of this repository for the User Entity"
// Derived query: looks up a user by email (the unique login identifier).
//  Returns an Optional (empty if none matches), so callers can handle the "not found" case safely.

// Derived query: returns all users whose token/session has been revoked (isRevoked = true).

 // Derived query: returns all users currently in the given status (e.g. active, suspended, inactive).

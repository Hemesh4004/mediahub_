package com.mediahub.iam;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handle(RuntimeException ex) {

        
        String message = ex.getMessage();
        int status;

        
        switch (message) {
            // 409 Conflict — the resource/state already exists or clashes with a current state.
            case "EMAIL_ALREADY_EXISTS":
            case "ROLE_ALREADY_EXISTS":
            case "PERMISSION_ALREADY_EXISTS":
            case "ALREADY_SUSPENDED":
            case "PERMISSION_ALREADY_ASSIGNED":
                status = 409; break;
            // 404 Not Found — the requested user, role, or permission does not exist.
            case "USER_NOT_FOUND":
            case "ROLE_NOT_FOUND":
            case "PERMISSION_NOT_FOUND":
                status = 404; break;
            // 401 Unauthorized — authentication failed or the refresh token is invalid.
            case "INVALID_CREDENTIALS":
            case "REFRESH_TOKEN_INVALID":
                status = 401; break;
            // 403 Forbidden — the account/action is not permitted in its current state.
            case "ACCOUNT_SUSPENDED":
            case "ACCOUNT_INACTIVE":
            case "CANNOT_SUSPEND_SELF":
                status = 403; break;
            // 400 Bad Request — the request is invalid given the resource's state.
            case "ALREADY_ACTIVE":
                status = 400; break;
            // Fallback for any unmapped error — surface it as a generic 500.
            default:
                status = 500;
        }

        // Wrap the chosen status and message into a JSON body ({"message": "..."})
        // and return it as the final response to the client.
        return ResponseEntity.status(status).body(
            Map.of("message", message)
        );
    }
}





























// Centralized exception handler applied across all REST controllers. @RestControllerAdvice
// lets it intercept exceptions thrown by any controller and return a uniform JSON error response.

// Catches every RuntimeException bubbling up from the controllers/services and
    // converts it into an HTTP response, so error handling lives in one place.

// Read the exception's message, which the services use as a machine-readable
        // error code, and prepare a variable to hold the matching HTTP status.

// Map each known error code to the appropriate HTTP status; any unrecognized
        // message falls through to the default and is treated as a 500 server error.
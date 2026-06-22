package com.mediahub.iam.dto;


public class LoginRequest {

    
    private String email;
    private String password;

    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}























// DTO (Data Transfer Object) carrying the request body for the login endpoint.
// Spring deserializes the incoming JSON into this object so the controller can read the credentials.

// The login credentials supplied by the client; both populated from the JSON request body.

// Getters and setters for email and password — Spring/Jackson uses these to map the JSON
    // fields onto the object during deserialization and to read the values back out.
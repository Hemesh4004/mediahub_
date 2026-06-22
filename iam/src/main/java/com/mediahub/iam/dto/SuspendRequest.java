package com.mediahub.iam.dto;


public class SuspendRequest {
    private String reason;

   
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}



























// DTO (Data Transfer Object) carrying the request body for the suspend / soft-delete user endpoints.
// Spring deserializes the incoming JSON into this object so the controller can read its field.

    // The reason for suspending/deactivating the user; populated from the JSON request body.

    // Getter and setter for "reason" — Spring/Jackson uses these to map the JSON field
    // to the object during deserialization and to read the value back out.

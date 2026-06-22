package com.mediahub.iam.dto;


public class ActivateRequest {
    private Long activatedBy;

    
    public Long getActivatedBy() { return activatedBy; }
    public void setActivatedBy(Long activatedBy) { this.activatedBy = activatedBy; }
}



























// DTO (Data Transfer Object) carrying the request body for the "activate user" endpoint.
// Spring deserializes the incoming JSON into this object so the controller can read its fields.

    // Id of the admin/actor performing the activation; populated from the JSON request body.

    // Getter and setter for "activatedBy" — Spring/Jackson uses these to map the JSON field
    // to the object during deserialization and to read the value back out.

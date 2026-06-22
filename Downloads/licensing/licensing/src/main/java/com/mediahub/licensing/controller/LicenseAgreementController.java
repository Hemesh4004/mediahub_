package com.mediahub.licensing.controller;

import com.mediahub.licensing.dto.request.LicenseAgreementRequestDTO;
import com.mediahub.licensing.dto.response.LicenseAgreementResponseDTO;
import com.mediahub.licensing.dto.response.LicenseExpiringSoonResponseDTO;
import com.mediahub.licensing.service.LicenseAgreementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mediaHub/contentLicensing")
public class LicenseAgreementController {

    @Autowired
    private LicenseAgreementService service;

    @PostMapping("/createLicense/v1.0")
    public ResponseEntity<?> create(@RequestBody LicenseAgreementRequestDTO dto) {
        service.createLicense(dto);
        return ResponseEntity.status(201).body("{\"message\": \"License created successfully\"}");
    }

    @GetMapping("/getAllLicenses/v1.0")
    public ResponseEntity<List<LicenseAgreementResponseDTO>> getAll(
            @RequestParam(required = false) String status) {
        if (status != null) {
            return ResponseEntity.ok(service.getByStatus(status));
        }
        return ResponseEntity.ok(service.getAllLicenses());
    }

    @GetMapping("/getExpiringSoonLicenses/v1.0")
    public ResponseEntity<List<LicenseExpiringSoonResponseDTO>>
            expiringSoon() {
        return ResponseEntity.ok(service.getExpiringSoon());
    }

    @GetMapping("/getLicense/v1.0/{id}")
    public ResponseEntity<LicenseAgreementResponseDTO> getById(@PathVariable Integer id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/updateLicense/v1.0/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestBody LicenseAgreementRequestDTO dto) {
        service.updateLicense(id, dto);
        return ResponseEntity.ok("{\"message\": \"License updated successfully\"}");
    }
}

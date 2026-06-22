package com.mediahub.licensing.service;

import com.mediahub.licensing.dto.request.LicenseAgreementRequestDTO;
import com.mediahub.licensing.dto.response.LicenseAgreementResponseDTO;
import com.mediahub.licensing.dto.response.LicenseExpiringSoonResponseDTO;
import com.mediahub.licensing.entity.LicenseAgreement;
import com.mediahub.licensing.repository.LicenseAgreementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LicenseAgreementService {

    @Autowired
    private LicenseAgreementRepository repo;

    public LicenseAgreementResponseDTO createLicense(LicenseAgreementRequestDTO dto) {
        LicenseAgreement entity = toEntity(dto);
        entity.setStatus("Active");
        return toResponseDTO(repo.save(entity));
    }

    public List<LicenseAgreementResponseDTO> getAllLicenses() {
        return repo.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public List<LicenseAgreementResponseDTO> getByStatus(String status) {
        return repo.findByStatus(status).stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public Optional<LicenseAgreementResponseDTO> getById(Integer id) {
        return repo.findById(id).map(this::toResponseDTO);
    }

 // Changed return type to LicenseExpiringSoonResponseDTO
    public List<LicenseExpiringSoonResponseDTO> getExpiringSoon() {
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysLater = today.plusDays(7);
        return repo.findExpiringSoon(today, sevenDaysLater)
            .stream()
            .map(this::toExpiringSoonDTO)  // ← new function
            .collect(Collectors.toList());
    }

    public LicenseAgreementResponseDTO updateLicense(Integer id, LicenseAgreementRequestDTO dto) {
        LicenseAgreement existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("License not found"));

        if (existing.getStatus().equals("Expired") || existing.getStatus().equals("Terminated")) {
            throw new RuntimeException("Cannot update Expired or Terminated license");
        }

        existing.setTerritory(dto.getTerritory());
        existing.setRightsType(dto.getRightsType());
        existing.setEndDate(dto.getEndDate());
        existing.setStatus(dto.getStatus());
        return toResponseDTO(repo.save(existing));
    }

    private LicenseAgreement toEntity(LicenseAgreementRequestDTO dto) {
        LicenseAgreement entity = new LicenseAgreement();
        entity.setContentId(dto.getContentId());
        entity.setLicensorId(dto.getLicensorId());
        entity.setLicenseeRef(dto.getLicenseeRef());
        entity.setTerritory(dto.getTerritory());
        entity.setRightsType(dto.getRightsType());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setLicenseFee(dto.getLicenseFee());
        return entity;
    }

    private LicenseAgreementResponseDTO toResponseDTO(LicenseAgreement entity) {
        LicenseAgreementResponseDTO dto = new LicenseAgreementResponseDTO();
        dto.setLicenseId(entity.getLicenseId());
        dto.setContentId(entity.getContentId());
        dto.setLicensorId(entity.getLicensorId());
        dto.setLicenseeRef(entity.getLicenseeRef());
        dto.setTerritory(entity.getTerritory());
        dto.setRightsType(entity.getRightsType());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setLicenseFee(entity.getLicenseFee());
        dto.setStatus(entity.getStatus());
        return dto;
    }
    
 // New conversion function — Entity to ExpiringSoonDTO
    private LicenseExpiringSoonResponseDTO toExpiringSoonDTO(
            LicenseAgreement entity) {
        LicenseExpiringSoonResponseDTO dto =
            new LicenseExpiringSoonResponseDTO();
        dto.setLicenseId(entity.getLicenseId());
        dto.setTerritory(entity.getTerritory());
        dto.setEndDate(entity.getEndDate());
        // Calculate days remaining — endDate minus today
        long days = ChronoUnit.DAYS.between(
            LocalDate.now(), entity.getEndDate());
        dto.setDaysRemaining(days);
        return dto;
    }    
}

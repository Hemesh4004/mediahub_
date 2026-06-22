package com.mediahub.licensing.service;

import com.mediahub.licensing.dto.request.TerritoryRestrictionRequestDTO;
import com.mediahub.licensing.dto.response.TerritoryRestrictionResponseDTO;
import com.mediahub.licensing.entity.TerritoryRestriction;
import com.mediahub.licensing.repository.TerritoryRestrictionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TerritoryRestrictionService {

    @Autowired
    private TerritoryRestrictionRepository repo;

    public TerritoryRestrictionResponseDTO createRestriction(TerritoryRestrictionRequestDTO dto) {
        TerritoryRestriction entity = toEntity(dto);
        entity.setStatus("Active");
        return toResponseDTO(repo.save(entity));
    }

    public List<TerritoryRestrictionResponseDTO> getByContentId(Integer contentId) {
        return repo.findByContentIdAndStatus(contentId, "Active").stream()
                .map(this::toResponseDTO).collect(Collectors.toList());
    }

    public TerritoryRestrictionResponseDTO updateRestriction(Integer id, TerritoryRestrictionRequestDTO dto) {
        TerritoryRestriction existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Restriction not found"));
        existing.setRestrictedCountries(dto.getRestrictedCountries());
        existing.setAllowedCountries(dto.getAllowedCountries());
        return toResponseDTO(repo.save(existing));
    }

    private TerritoryRestriction toEntity(TerritoryRestrictionRequestDTO dto) {
        TerritoryRestriction entity = new TerritoryRestriction();
        entity.setContentId(dto.getContentId());
        entity.setRestrictedCountries(dto.getRestrictedCountries());
        entity.setAllowedCountries(dto.getAllowedCountries());
        entity.setEffectiveDate(dto.getEffectiveDate());
        return entity;
    }

    private TerritoryRestrictionResponseDTO toResponseDTO(TerritoryRestriction entity) {
        TerritoryRestrictionResponseDTO dto = new TerritoryRestrictionResponseDTO();
        dto.setRestrictionId(entity.getRestrictionId());
        dto.setContentId(entity.getContentId());
        dto.setRestrictedCountries(entity.getRestrictedCountries());
        dto.setAllowedCountries(entity.getAllowedCountries());
        dto.setEffectiveDate(entity.getEffectiveDate());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}

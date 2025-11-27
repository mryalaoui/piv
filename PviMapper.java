package com.bnpparibas.dsibddf.fis.pvi.mapper;

import com.bnpparibas.dsibddf.fis.pvi.api.dto.PviSimulationRequestDto;
import com.bnpparibas.dsibddf.fis.pvi.api.dto.PviSimulationResponseDto;
import com.bnpparibas.dsibddf.fis.pvi.domain.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PviMapper {

    public PviSimulationRequest toDomain(PviSimulationRequestDto dto) {
        return new PviSimulationRequest(
                TypeBien.valueOf(dto.getTypeBien()),
                UsageBien.valueOf(dto.getUsageBien()),
                OrigineBien.valueOf(dto.getOrigineBien()),
                LocalDate.parse(dto.getDateOrigine()),
                LocalDate.parse(dto.getDateVente()),
                dto.getPrixOrigine(),
                dto.getFraisOrigine(),
                dto.getFraisReels(),
                dto.getPrixVente(),
                dto.getFraisVente(),
                TravauxFlag.fromString(dto.getTravaux()),
                dto.getMontantTravaux()
        );
    }

    public PviSimulationResponseDto toDto(PviSimulationResult result) {
        PviSimulationResponseDto dto = new PviSimulationResponseDto();
        dto.setDetentionAnnee(result.getDetentionAnnee());
        dto.setPrixCession(result.getPrixCession());
        dto.setPlusValueBrute(result.getPlusValueBrute());
        dto.setPlusValueNette(result.getPlusValueNette());
        dto.setPrixRevient(result.getPrixRevient());
        dto.setImpotRevenu(result.getImpotRevenu());
        dto.setPrelevementsSociaux(result.getPrelevementsSociaux());
        dto.setTaxePlusValue(result.getTaxePlusValue());
        dto.setImpotsTotaux(result.getImpotsTotaux());
        dto.setNetDispo(result.getNetDispo());
        dto.setPressionFiscale(result.getPressionFiscale());
        dto.setTaxOutcomeState(result.getTaxOutcomeState().name());
        dto.setMessageTravaux(result.getMessageTravaux());
        dto.setMessageAnniversaire(result.getMessageAnniversaire());
        dto.setSuggestions(result.getSuggestions());
        return dto;
    }
}

package com.bnpparibas.dsibddf.fis.pvi.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(name = "PviSimulationResponse")
public class PviSimulationResponseDto {

    private int detentionAnnee;
    private BigDecimal prixCession;
    private BigDecimal plusValueBrute;
    private BigDecimal plusValueNette;
    private BigDecimal prixRevient;

    private BigDecimal impotRevenu;
    private BigDecimal prelevementsSociaux;
    private BigDecimal taxePlusValue;
    private BigDecimal impotsTotaux;
    private BigDecimal netDispo;
    private int pressionFiscale;

    private String taxOutcomeState;

    private String messageTravaux;
    private String messageAnniversaire;
    private List<String> suggestions;
}

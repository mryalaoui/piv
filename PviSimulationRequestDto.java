package com.bnpparibas.dsibddf.fis.pvi.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "PviSimulationRequest")
public class PviSimulationRequestDto {

    @Schema(example = "APPARTEMENT")
    private String typeBien;

    @Schema(example = "RESIDENCE_SECONDAIRE")
    private String usageBien;

    @Schema(example = "ACHAT")
    private String origineBien;

    @Schema(example = "2013-03-15", description = "Date d’acquisition (ISO-8601)")
    private String dateOrigine;

    @Schema(example = "2025-09-10", description = "Date de vente (ISO-8601)")
    private String dateVente;

    @Schema(example = "150000")
    private BigDecimal prixOrigine;

    @Schema(example = "0")
    private BigDecimal fraisOrigine;

    @Schema(example = "0")
    private BigDecimal fraisReels;

    @Schema(example = "280000")
    private BigDecimal prixVente;

    @Schema(example = "0")
    private BigDecimal fraisVente;

    @Schema(example = "NON", allowableValues = {"OUI", "NON"})
    private String travaux;

    @Schema(example = "0")
    private BigDecimal montantTravaux;
}

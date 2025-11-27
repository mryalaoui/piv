package com.bnpparibas.dsibddf.fis.pvi.domain.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PviSimulationResult {

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

    private TaxOutcomeState taxOutcomeState;

    private String messageTravaux;
    private String messageAnniversaire;
    private List<String> suggestions;

    // Méthodes statiques utilitaires pour cas non imposables,
    // qui reproduisent la logique PHP (moins-value, seuil, RP)
}

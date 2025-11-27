package com.bnpparibas.dsibddf.fis.pvi.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PviSimulationRequest(
        TypeBien typeBien,
        UsageBien usageBien,
        OrigineBien origineBien,
        LocalDate dateOrigine,
        LocalDate dateVente,
        BigDecimal prixOrigine,
        BigDecimal fraisOrigine,
        BigDecimal fraisReels,
        BigDecimal prixVente,
        BigDecimal fraisVente,
        TravauxFlag travaux,
        BigDecimal montantTravaux
) {}

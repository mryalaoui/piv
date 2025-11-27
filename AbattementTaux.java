package com.bnpparibas.dsibddf.fis.pvi.domain.model;

import java.math.BigDecimal;

public record AbattementTaux(
        BigDecimal tauxAnnuel,
        BigDecimal tauxAbattementGlobal
) {}

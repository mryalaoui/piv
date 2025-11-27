package com.bnpparibas.dsibddf.fis.pvi.domain.model;

import java.math.BigDecimal;

public record TaxePlusValueBracket(
        BigDecimal min,
        BigDecimal max, // peut être null si "max" = "-"
        BigDecimal pourcentagePlusValue,
        BigDecimal rapport
) {}

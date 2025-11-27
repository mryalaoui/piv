package com.bnpparibas.dsibddf.fis.pvi.domain.service;

import com.bnpparibas.dsibddf.fis.pvi.domain.model.AbattementYear;
import com.bnpparibas.dsibddf.fis.pvi.domain.model.TaxOutcomeState;
import lombok.Builder;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Builder
public record TaxBreakdown(
        BigDecimal baseIr,
        BigDecimal basePs,
        BigDecimal impotRevenu,
        BigDecimal prelevementsSociaux,
        BigDecimal taxePlusValue,
        BigDecimal total
) {}


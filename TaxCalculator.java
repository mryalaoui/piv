package com.bnpparibas.dsibddf.fis.pvi.domain.service;

import com.bnpparibas.dsibddf.fis.pvi.domain.model.AbattementYear;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class TaxCalculator {

    private static final BigDecimal TAUX_IR = new BigDecimal("0.19");
    private static final BigDecimal TAUX_PS = new BigDecimal("0.172");

    private final AbattementService abattementService;
    private final TaxePlusValueService taxePlusValueService;

    public TaxBreakdown compute(BigDecimal plusValueBrute,
                                int detentionAnnee,
                                boolean isResidencePrincipale,
                                BigDecimal prixVente) {

        if (plusValueBrute.signum() <= 0) {
            return zero();
        }

        // seuil prix de vente
        if (prixVente.compareTo(new BigDecimal("15000")) <= 0) {
            return zero();
        }

        if (isResidencePrincipale) {
            return zero();
        }

        AbattementYear abattement = abattementService.getAbattementFor(detentionAnnee);

        BigDecimal baseIr = applyAbattement(plusValueBrute, abattement.IR().tauxAbattementGlobal());
        BigDecimal basePs = applyAbattement(plusValueBrute, abattement.PS().tauxAbattementGlobal());

        // Exonération par durée
        if (detentionAnnee > 30) {
            baseIr = BigDecimal.ZERO;
            basePs = BigDecimal.ZERO;
        } else if (detentionAnnee > 22) {
            baseIr = BigDecimal.ZERO;
        }

        BigDecimal ir = baseIr.multiply(TAUX_IR).setScale(0, RoundingMode.HALF_UP);
        BigDecimal ps = basePs.multiply(TAUX_PS).setScale(0, RoundingMode.HALF_UP);
        BigDecimal taxePv = taxePlusValueService.computeTaxePlusValue(baseIr);

        BigDecimal total = ir.add(ps).add(taxePv);

        return TaxBreakdown.builder()
                .baseIr(baseIr)
                .basePs(basePs)
                .impotRevenu(ir)
                .prelevementsSociaux(ps)
                .taxePlusValue(taxePv)
                .total(total)
                .build();
    }

    private BigDecimal applyAbattement(BigDecimal pvBrute, java.math.BigDecimal tauxAbattementGlobal) {
        if (pvBrute.signum() <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal abatt = tauxAbattementGlobal
                .divide(new BigDecimal("100"))
                .multiply(pvBrute);
        return pvBrute.subtract(abatt).setScale(0, RoundingMode.HALF_UP);
    }

    private TaxBreakdown zero() {
        return TaxBreakdown.builder()
                .baseIr(BigDecimal.ZERO)
                .basePs(BigDecimal.ZERO)
                .impotRevenu(BigDecimal.ZERO)
                .prelevementsSociaux(BigDecimal.ZERO)
                .taxePlusValue(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .build();
    }
}

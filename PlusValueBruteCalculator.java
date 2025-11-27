package com.bnpparibas.dsibddf.fis.pvi.domain.service;

import com.bnpparibas.dsibddf.fis.pvi.domain.model.PviSimulationRequest;
import com.bnpparibas.dsibddf.fis.pvi.domain.model.TypeBien;
import com.bnpparibas.dsibddf.fis.pvi.domain.model.TravauxFlag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
public class PlusValueBruteCalculator {

    private static final BigDecimal TAUX_FRAIS_ACHAT = new BigDecimal("0.075");
    private static final BigDecimal TAUX_TRAVAUX_FORFAIT = new BigDecimal("0.15");

    public BigDecimal calculate(PviSimulationRequest req, int detentionAnnee) {

        BigDecimal prixOrigine = nvl(req.prixOrigine());
        BigDecimal fraisReels = nvl(req.fraisReels());
        BigDecimal fraisOrigine;

        // PHP : if (origineBien == 'achat') { max(7.5% prixOrigine, fraisReels) } else { fraisOrigine saisi }
        if (req.origineBien() == com.bnpparibas.dsibddf.fis.pvi.domain.model.OrigineBien.ACHAT) {
            BigDecimal forfait = prixOrigine.multiply(TAUX_FRAIS_ACHAT);
            fraisOrigine = forfait.max(fraisReels);
        } else {
            fraisOrigine = nvl(req.fraisOrigine());
        }

        BigDecimal montantTravaux = (req.travaux() == TravauxFlag.OUI) ? nvl(req.montantTravaux()) : BigDecimal.ZERO;

        BigDecimal majorationTravaux;
        // PHP : ($differenceAnnee >= 5 && typeBien != 'terrain') ? max(15% prixOrigine, montantTravaux) : montantTravaux;
        if (detentionAnnee >= 5 && req.typeBien() != TypeBien.TERRAIN) {
            BigDecimal forfaitTravaux = prixOrigine.multiply(TAUX_TRAVAUX_FORFAIT);
            majorationTravaux = forfaitTravaux.max(montantTravaux);
        } else {
            majorationTravaux = montantTravaux;
        }

        BigDecimal prixOrigineNet = prixOrigine
                .add(fraisOrigine)
                .add(majorationTravaux);

        BigDecimal prixVenteNet = nvl(req.prixVente()).subtract(nvl(req.fraisVente()));

        return prixVenteNet.subtract(prixOrigineNet).setScale(0, RoundingMode.HALF_UP);
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}

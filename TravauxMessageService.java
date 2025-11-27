package com.bnpparibas.dsibddf.fis.pvi.domain.service;

import com.bnpparibas.dsibddf.fis.pvi.domain.model.PviSimulationRequest;
import com.bnpparibas.dsibddf.fis.pvi.domain.model.TravauxFlag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TravauxMessageService {

    private static final BigDecimal TAUX_TRAVAUX_FORFAIT = new BigDecimal("0.15");

    /**
     * Reproduit la logique PHP :
     * if (detentionAnnee > 5 && montantTravaux < prixOrigine * 0.15) => message
     */
    public String buildMessage(PviSimulationRequest req, int detentionAnnee) {

        BigDecimal prixOrigine = nvl(req.prixOrigine());

        BigDecimal montantTravaux = (req.travaux() == TravauxFlag.OUI)
                ? nvl(req.montantTravaux())
                : BigDecimal.ZERO;

        BigDecimal abattementForfaitaire = prixOrigine.multiply(TAUX_TRAVAUX_FORFAIT);

        if (detentionAnnee > 5 && montantTravaux.compareTo(abattementForfaitaire) < 0) {
            String formatted = formatEuro(abattementForfaitaire);
            return "La durée de détention étant supérieure à 5 ans, le prix d'acquisition est majoré de "
                    + "15 % pour travaux, soit " + formatted + " €.";
        }

        return null;
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String formatEuro(BigDecimal value) {
        NumberFormat nf = NumberFormat.getInstance(Locale.FRANCE);
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);
        return nf.format(value);
    }
}

package com.bnpparibas.dsibddf.fis.pvi.domain.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class AnniversaryService {

    /**
     * Reproduit la logique PHP :
     *
     * - On prend l'année courante (comme date('Y') dans PHP)
     * - On prend le mois + jour de la date d'origine
     * - On ajoute +1 jour
     * - Si cette date est < date de vente saisie, on passe à l'année suivante
     */
    public LocalDate computeAnniversaryDate(LocalDate dateOrigine, LocalDate dateVente) {

        int currentYear = LocalDate.now().getYear(); // même idée que date('Y')

        LocalDate anniv = LocalDate.of(
                currentYear,
                dateOrigine.getMonth(),
                dateOrigine.getDayOfMonth()
        ).plusDays(1); // +1 jour comme en PHP

        if (anniv.isBefore(dateVente)) {
            anniv = anniv.plusYears(1);
        }

        return anniv;
    }

    /**
     * Construit le message d'économie d'impôts,
     * à partir de l'impôt actuel et de l'impôt après report à la date anniversaire.
     */
    public String buildMessage(LocalDate anniversaryDate,
                               BigDecimal currentTax,
                               BigDecimal newTax) {

        if (currentTax == null || currentTax.signum() == 0) {
            return null;
        }
        if (newTax == null || newTax.compareTo(currentTax) >= 0) {
            return null; // pas d'économie -> pas de message
        }

        BigDecimal economy = currentTax.subtract(newTax)
                .setScale(0, RoundingMode.HALF_UP);

        // Si jamais tu veux la phrase 100 % identique au PHP,
        // tu peux adapter le texte ici
        return "Si vous attendiez le " + anniversaryDate +
                " pour vendre (date anniversaire d'acquisition du bien), " +
                "vous pourriez économiser " + economy + " €.";
    }
}

package com.bnpparibas.dsibddf.fis.pvi.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PviSimulationRequestTest {

    @Test
    void testCompleteRequest() {
        PviSimulationRequest r = new PviSimulationRequest(
                TypeBien.APPARTEMENT,
                UsageBien.RESIDENCE_SECONDAIRE,
                OrigineBien.ACHAT,
                LocalDate.of(2010, 5, 10),
                LocalDate.of(2025, 9, 10),
                new BigDecimal("200000"),
                new BigDecimal("15000"),
                new BigDecimal("0"),
                new BigDecimal("350000"),
                new BigDecimal("10000"),
                TravauxFlag.OUI,
                new BigDecimal("30000")
        );

        assertEquals(TypeBien.APPARTEMENT, r.typeBien());
        assertEquals(UsageBien.RESIDENCE_SECONDAIRE, r.usageBien());
        assertEquals(OrigineBien.ACHAT, r.origineBien());
        assertEquals(LocalDate.of(2010, 5, 10), r.dateOrigine());
        assertEquals(LocalDate.of(2025, 9, 10), r.dateVente());
        assertEquals(new BigDecimal("200000"), r.prixOrigine());
        assertEquals(new BigDecimal("15000"), r.fraisOrigine());
        assertEquals(new BigDecimal("0"), r.fraisReels());
        assertEquals(new BigDecimal("350000"), r.prixVente());
        assertEquals(new BigDecimal("10000"), r.fraisVente());
        assertEquals(TravauxFlag.OUI, r.travaux());
        assertEquals(new BigDecimal("30000"), r.montantTravaux());
    }
}


package com.bnpparibas.dsibddf.fis.pvi.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PviSimulationResultTest {

    @Test
    void testCompleteResult() {
        PviSimulationResult r = PviSimulationResult.builder()
                .detentionAnnee(12)
                .prixCession(new BigDecimal("350000"))
                .plusValueBrute(new BigDecimal("150000"))
                .plusValueNette(new BigDecimal("120000"))
                .prixRevient(new BigDecimal("200000"))
                .impotRevenu(new BigDecimal("18000"))
                .prelevementsSociaux(new BigDecimal("20400"))
                .taxePlusValue(new BigDecimal("5000"))
                .impotsTotaux(new BigDecimal("43400"))
                .netDispo(new BigDecimal("306600"))
                .pressionFiscale(28)
                .taxOutcomeState(TaxOutcomeState.IMPOSABLE_IR_PS_SURTAXE)
                .messageTravaux("Forfait 15 % appliqué")
                .messageAnniversaire("Attendre 1 an permet une exonération totale")
                .suggestions(List.of("Optimiser la date de vente", "Vérifier l'abattement"))
                .build();

        assertEquals(12, r.getDetentionAnnee());
        assertEquals(new BigDecimal("350000"), r.getPrixCession());
        assertEquals(new BigDecimal("150000"), r.getPlusValueBrute());
        assertEquals(new BigDecimal("120000"), r.getPlusValueNette());
        assertEquals(new BigDecimal("200000"), r.getPrixRevient());
        assertEquals(new BigDecimal("18000"), r.getImpotRevenu());
        assertEquals(new BigDecimal("20400"), r.getPrelevementsSociaux());
        assertEquals(new BigDecimal("5000"), r.getTaxePlusValue());
        assertEquals(new BigDecimal("43400"), r.getImpotsTotaux());
        assertEquals(new BigDecimal("306600"), r.getNetDispo());
        assertEquals(28, r.getPressionFiscale());
        assertEquals(TaxOutcomeState.IMPOSABLE_IR_PS_SURTAXE, r.getTaxOutcomeState());
        assertEquals("Forfait 15 % appliqué", r.getMessageTravaux());
        assertEquals("Attendre 1 an permet une exonération totale", r.getMessageAnniversaire());
        assertEquals(List.of("Optimiser la date de vente", "Vérifier l'abattement"), r.getSuggestions());
    }
}






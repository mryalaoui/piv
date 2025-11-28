package com.bnpparibas.dsibddf.fis.pvi.domain.service;

import com.bnpparibas.dsibddf.fis.pvi.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PlusValueBruteCalculatorTest {

    private PlusValueBruteCalculator calc;

    @BeforeEach
    void setup() {
        calc = new PlusValueBruteCalculator();
    }

    private PviSimulationRequest req(
            TypeBien typeBien,
            OrigineBien origine,
            TravauxFlag travaux,
            BigDecimal prixOrigine,
            BigDecimal fraisOrigine,
            BigDecimal fraisReels,
            BigDecimal montantTravaux,
            BigDecimal prixVente,
            BigDecimal fraisVente
    ) {
        return new PviSimulationRequest(
                typeBien,
                UsageBien.RESIDENCE_SECONDAIRE,
                origine,
                LocalDate.of(2010, 1, 1),
                LocalDate.of(2025, 1, 1),
                prixOrigine,
                fraisOrigine,
                fraisReels,
                prixVente,
                fraisVente,
                travaux,
                montantTravaux
        );
    }

    @Test
    void cas1_achat_forfait_superieur_aux_frais_reels() {
        var r = req(TypeBien.APPARTEMENT, OrigineBien.ACHAT, TravauxFlag.NON,
                new BigDecimal("100000"), BigDecimal.ZERO, new BigDecimal("1000"),
                BigDecimal.ZERO, new BigDecimal("200000"), BigDecimal.ZERO);
        assertEquals(new BigDecimal("200000")
                        .subtract(new BigDecimal("100000")
                                .add(new BigDecimal("7500"))),
                calc.calculate(r, 0));
    }

    @Test
    void cas2_achat_frais_reels_superieurs_au_forfait() {
        var r = req(TypeBien.APPARTEMENT, OrigineBien.ACHAT, TravauxFlag.NON,
                new BigDecimal("100000"), BigDecimal.ZERO, new BigDecimal("10000"),
                BigDecimal.ZERO, new BigDecimal("200000"), BigDecimal.ZERO);
        assertEquals(new BigDecimal("200000")
                        .subtract(new BigDecimal("100000")
                                .add(new BigDecimal("10000"))),
                calc.calculate(r, 0));
    }

    @Test
    void cas3_non_achat_frais_origine_non_nuls() {
        var r = req(TypeBien.APPARTEMENT, OrigineBien.DONATION, TravauxFlag.NON,
                new BigDecimal("100000"), new BigDecimal("8000"), BigDecimal.ZERO,
                BigDecimal.ZERO, new BigDecimal("200000"), BigDecimal.ZERO);
        assertEquals(new BigDecimal("200000")
                        .subtract(new BigDecimal("100000")
                                .add(new BigDecimal("8000"))),
                calc.calculate(r, 0));
    }

    @Test
    void cas4_non_achat_frais_origine_nuls() {
        var r = req(TypeBien.APPARTEMENT, OrigineBien.SUCCESSION, TravauxFlag.NON,
                new BigDecimal("100000"), null, BigDecimal.ZERO,
                BigDecimal.ZERO, new BigDecimal("200000"), BigDecimal.ZERO);
        assertEquals(new BigDecimal("200000")
                        .subtract(new BigDecimal("100000")),
                calc.calculate(r, 0));
    }

    @Test
    void cas5_travaux_non_montant_zero() {
        var r = req(TypeBien.APPARTEMENT, OrigineBien.ACHAT, TravauxFlag.NON,
                new BigDecimal("100000"), BigDecimal.ZERO, new BigDecimal("10000"),
                new BigDecimal("50000"), new BigDecimal("200000"), BigDecimal.ZERO);
        assertEquals(new BigDecimal("200000")
                        .subtract(new BigDecimal("100000")
                                .add(new BigDecimal("10000"))),
                calc.calculate(r, 0));
    }

    @Test
    void cas6_travaux_oui_detention_inferieure_5() {
        var r = req(TypeBien.APPARTEMENT, OrigineBien.ACHAT, TravauxFlag.OUI,
                new BigDecimal("100000"), BigDecimal.ZERO, new BigDecimal("10000"),
                new BigDecimal("5000"), new BigDecimal("200000"), BigDecimal.ZERO);
        assertEquals(new BigDecimal("200000")
                        .subtract(new BigDecimal("100000")
                                .add(new BigDecimal("10000"))
                                .add(new BigDecimal("5000"))),
                calc.calculate(r, 4));
    }

    @Test
    void cas7_travaux_oui_detention_5_forfait_superieur() {
        var r = req(TypeBien.APPARTEMENT, OrigineBien.ACHAT, TravauxFlag.OUI,
                new BigDecimal("100000"), BigDecimal.ZERO, new BigDecimal("1000"),
                new BigDecimal("5000"), new BigDecimal("200000"), BigDecimal.ZERO);
        assertEquals(new BigDecimal("200000")
                        .subtract(new BigDecimal("100000")
                                .add(new BigDecimal("7500"))
                                .add(new BigDecimal("15000"))),
                calc.calculate(r, 5));
    }

    @Test
    void cas8_travaux_oui_detention_5_travaux_superieurs() {
        var r = req(TypeBien.APPARTEMENT, OrigineBien.ACHAT, TravauxFlag.OUI,
                new BigDecimal("100000"), BigDecimal.ZERO, new BigDecimal("1000"),
                new BigDecimal("50000"), new BigDecimal("200000"), BigDecimal.ZERO);
        assertEquals(new BigDecimal("200000")
                        .subtract(new BigDecimal("100000")
                                .add(new BigDecimal("7500"))
                                .add(new BigDecimal("50000"))),
                calc.calculate(r, 5));
    }

    @Test
    void cas9_travaux_oui_detention_5_terrain_pas_de_forfait() {
        var r = req(TypeBien.TERRAIN, OrigineBien.ACHAT, TravauxFlag.OUI,
                new BigDecimal("100000"), BigDecimal.ZERO, new BigDecimal("1000"),
                new BigDecimal("20000"), new BigDecimal("200000"), BigDecimal.ZERO);
        assertEquals(new BigDecimal("200000")
                        .subtract(new BigDecimal("100000")
                                .add(new BigDecimal("7500"))
                                .add(new BigDecimal("20000"))),
                calc.calculate(r, 5));
    }

    @Test
    void cas10_frais_vente_zero() {
        var r = req(TypeBien.APPARTEMENT, OrigineBien.ACHAT, TravauxFlag.NON,
                new BigDecimal("100000"), BigDecimal.ZERO, new BigDecimal("1000"),
                BigDecimal.ZERO, new BigDecimal("200000"), BigDecimal.ZERO);
        assertEquals(new BigDecimal("200000")
                        .subtract(new BigDecimal("100000")
                                .add(new BigDecimal("7500"))),
                calc.calculate(r, 0));
    }

    @Test
    void cas11_frais_vente_superieurs_zero() {
        var r = req(TypeBien.APPARTEMENT, OrigineBien.ACHAT, TravauxFlag.NON,
                new BigDecimal("100000"), BigDecimal.ZERO, new BigDecimal("1000"),
                BigDecimal.ZERO, new BigDecimal("200000"), new BigDecimal("10000"));
        assertEquals(new BigDecimal("190000")
                        .subtract(new BigDecimal("100000")
                                .add(new BigDecimal("7500"))),
                calc.calculate(r, 0));
    }

    @Test
    void cas12_valeurs_null_nvl() {
        var r = req(TypeBien.APPARTEMENT, OrigineBien.SUCCESSION, TravauxFlag.NON,
                null, null, null, null, null, null);
        assertEquals(BigDecimal.ZERO, calc.calculate(r, 0));
    }

    @Test
    void cas13_resultat_plus_value_positive() {
        var r = req(TypeBien.APPARTEMENT, OrigineBien.ACHAT, TravauxFlag.NON,
                new BigDecimal("50000"), BigDecimal.ZERO, new BigDecimal("1000"),
                BigDecimal.ZERO, new BigDecimal("150000"), BigDecimal.ZERO);
        assertTrue(calc.calculate(r, 0).compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void cas14_resultat_moins_value() {
        var r = req(TypeBien.APPARTEMENT, OrigineBien.ACHAT, TravauxFlag.NON,
                new BigDecimal("200000"), BigDecimal.ZERO, new BigDecimal("10000"),
                BigDecimal.ZERO, new BigDecimal("150000"), BigDecimal.ZERO);
        assertTrue(calc.calculate(r, 0).compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    void cas15_arrondi_half_up() {
        var r = req(TypeBien.APPARTEMENT, OrigineBien.ACHAT, TravauxFlag.NON,
                new BigDecimal("100000"), BigDecimal.ZERO, new BigDecimal("0"),
                BigDecimal.ZERO, new BigDecimal("107500.4"), BigDecimal.ZERO);
        assertEquals(new BigDecimal("7500"), calc.calculate(r, 0));

        var r2 = req(TypeBien.APPARTEMENT, OrigineBien.ACHAT, TravauxFlag.NON,
                new BigDecimal("100000"), BigDecimal.ZERO, new BigDecimal("0"),
                BigDecimal.ZERO, new BigDecimal("107500.5"), BigDecimal.ZERO);
        assertEquals(new BigDecimal("7501"), calc.calculate(r2, 0));
    }
}

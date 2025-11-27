package com.bnpparibas.dsibddf.fis.pvi.domain.service;

import com.bnpparibas.dsibddf.fis.pvi.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PviSimulationService {

    private final PlusValueBruteCalculator plusValueBruteCalculator;
    private final TaxCalculator taxCalculator;
    private final AnniversaryService anniversaryService;
    private final TravauxMessageService travauxMessageService;
    private final SuggestionEngine suggestionEngine;

    public PviSimulationResult simulate(PviSimulationRequest req) {
        return simulateInternal(req, true); // avec calcul anniversaire
    }

    private PviSimulationResult simulateInternal(PviSimulationRequest req, boolean withAnniversary) {

        int detention = DetentionCalculator.yearsBetween(req.dateOrigine(), req.dateVente());

        var prixCession = nvl(req.prixVente()).subtract(nvl(req.fraisVente()));
        var plusValueBrute = plusValueBruteCalculator.calculate(req, detention);
        var prixRevient = prixCession.subtract(plusValueBrute);

        var taxes = taxCalculator.compute(
                plusValueBrute,
                detention,
                req.usageBien() == UsageBien.RESIDENCE_PRINCIPALE,
                nvl(req.prixVente())
        );

        var state = resolveTaxOutcomeState(plusValueBrute, nvl(req.prixVente()), req.usageBien(), detention, taxes);
        var netDispo = nvl(req.prixVente()).subtract(taxes.total());

        int pressionFiscale = (plusValueBrute.signum() > 0 && taxes.total().signum() > 0)
                ? taxes.total().multiply(new BigDecimal("100"))
                .divide(plusValueBrute, 0, RoundingMode.HALF_UP)
                .intValue()
                : 0;

        var builder = PviSimulationResult.builder()
                .detentionAnnee(detention)
                .prixCession(prixCession)
                .plusValueBrute(plusValueBrute)
                .plusValueNette(taxes.baseIr())
                .prixRevient(prixRevient)
                .impotRevenu(taxes.impotRevenu())
                .prelevementsSociaux(taxes.prelevementsSociaux())
                .taxePlusValue(taxes.taxePlusValue())
                .impotsTotaux(taxes.total())
                .netDispo(netDispo)
                .pressionFiscale(pressionFiscale)
                .taxOutcomeState(state);

        // Message travaux
        String worksMsg = travauxMessageService.buildMessage(req, detention);
        builder.messageTravaux(worksMsg);

        // Message anniversaire : on NE le fait que si withAnniversary = true
        String anniversaireMsg = null;
        if (withAnniversary && taxes.total().signum() > 0 && detention >= 4 && detention < 30) {
            anniversaireMsg = computeAnniversaryMessage(req, taxes.total());
        }
        builder.messageAnniversaire(anniversaireMsg);

        PviSimulationResult result = builder.build();

        // Suggestions (à partir du résultat final)
        result.setSuggestions(suggestionEngine.computeSuggestions(req, result));

        return result;
    }

    private String computeAnniversaryMessage(PviSimulationRequest req, BigDecimal currentTax) {

        var anniversaryDate = anniversaryService.computeAnniversaryDate(
                req.dateOrigine(),
                req.dateVente()
        );

        // On crée une nouvelle requête avec la date de vente = date anniversaire
        var newReq = new PviSimulationRequest(
                req.typeBien(),
                req.usageBien(),
                req.origineBien(),
                req.dateOrigine(),
                anniversaryDate,
                req.prixOrigine(),
                req.fraisOrigine(),
                req.fraisReels(),
                req.prixVente(),
                req.fraisVente(),
                req.travaux(),
                req.montantTravaux()
        );

        // Re-simulation SANS re-calcul d’anniversaire (withAnniversary = false)
        PviSimulationResult newSim = simulateInternal(newReq, false);

        BigDecimal newTax = newSim.getImpotsTotaux();

        return anniversaryService.buildMessage(anniversaryDate, currentTax, newTax);
    }

    private TaxOutcomeState resolveTaxOutcomeState(BigDecimal pvBrute,
                                                   BigDecimal prixVente,
                                                   UsageBien usage,
                                                   int detention,
                                                   TaxBreakdown taxes) {
        if (pvBrute.signum() <= 0) {
            return TaxOutcomeState.NON_IMPOSABLE_MOINS_VALUE;
        }
        if (prixVente.compareTo(new BigDecimal("15000")) <= 0) {
            return TaxOutcomeState.NON_IMPOSABLE_SEUIL_PRIX_VENTE;
        }
        if (usage == UsageBien.RESIDENCE_PRINCIPALE) {
            return TaxOutcomeState.NON_IMPOSABLE_RESIDENCE_PRINCIPALE;
        }
        if (detention > 30) {
            return TaxOutcomeState.NON_IMPOSABLE_EXONERATION_DUREE;
        }
        if (detention > 22) {
            return TaxOutcomeState.IMPOSABLE_PS_SEULEMENT;
        }
        if (taxes.taxePlusValue().signum() > 0) {
            return TaxOutcomeState.IMPOSABLE_IR_PS_SURTAXE;
        }
        return TaxOutcomeState.IMPOSABLE_IR_PS;
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}

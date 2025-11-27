package com.bnpparibas.dsibddf.fis.pvi.api;

import com.bnpparibas.dsibddf.fis.pvi.PviApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = PviApplication.class)
@AutoConfigureMockMvc
class PviSimulationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void simulateAppartementResidenceSecondaire_RealResponseJson() throws Exception {

        String inputJson = """
        {
          "typeBien": "APPARTEMENT",
          "usageBien": "RESIDENCE_SECONDAIRE",
          "origineBien": "ACHAT",
          "dateOrigine": "2021-11-26",
          "dateVente": "2025-11-26",
          "prixOrigine": 100000,
          "fraisOrigine": 0,
          "fraisReels": 0,
          "prixVente": 167500,
          "fraisVente": 0,
          "travaux": "NON",
          "montantTravaux": 0
        }
        """;

        String expectedJson = """
        {
          "detentionAnnee": 4,
          "prixCession": 167500,
          "plusValueBrute": 60000,
          "plusValueNette": 60000,
          "prixRevient": 107500,
          "impotRevenu": 11400,
          "prelevementsSociaux": 10320,
          "taxePlusValue": 1200,
          "impotsTotaux": 22920,
          "netDispo": 144580,
          "pressionFiscale": 38,
          "taxOutcomeState": "IMPOSABLE_IR_PS_SURTAXE",
          "messageTravaux": null,
          "messageAnniversaire": null,
          "suggestions": [
            "Le montant de l'imposition estimée inclut une surtaxe de 2 à 6 %, dont le montant serait susceptible de varier en cas de pluralité de cédants.",
            "L'impôt afférent à la plus-value est dû lors de la cession de l'immeuble et acquitté par le notaire.",
            "En cas de cession d'un bien démembré, le nu-propriétaire doit obtenir l'accord de l'usufruitier.",
            "La décision de la cession d'un bien immobilier détenu en indivision doit être prise à l'unanimité."
          ]
        }
        """;

        mockMvc.perform(
                        post("/api/v1/pvi/simulate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(inputJson)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, true));
    }
}

package com.bnpparibas.dsibddf.fis.pvi.cucumber.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class PviSteps {

    @Autowired
    private MockMvc mockMvc;

    // Table d'entrée (les scénarios PVI)
    private List<Map<String, String>> inputRows;

    // Résultats attendus indexés par id de scénario
    private final Map<String, Map<String, String>> expectedById = new HashMap<>();

    // Résultats réels de l'API indexés par id de scénario
    private final Map<String, JSONObject> actualResultsById = new HashMap<>();

    // -------------------------------------------------
    // GIVEN : scénarios + résultats attendus
    // -------------------------------------------------

    @Given("les scénarios PVI suivants :")
    public void les_scenarios_pvi_suivants(DataTable table) {
        this.inputRows = table.asMaps(String.class, String.class);
    }

    @Given("les résultats attendus pour chaque scénario :")
    public void les_resultats_attendus_pour_chaque_scenario(DataTable table) {
        this.expectedById.clear();
        for (Map<String, String> row : table.asMaps(String.class, String.class)) {
            String id = row.get("id");
            expectedById.put(id, row);
        }
    }

    // -------------------------------------------------
    // WHEN : on appelle l'API pour tous les scénarios
    // -------------------------------------------------

    @When("je lance la simulation PVI pour tous les scénarios")
    public void je_lance_la_simulation_pvi_pour_tous_les_scenarios() throws Exception {
        this.actualResultsById.clear();

        for (Map<String, String> row : inputRows) {
            String id = row.get("id");

            String json = """
                {
                  "typeBien": "%s",
                  "usageBien": "%s",
                  "origineBien": "%s",
                  "dateOrigine": "%s",
                  "dateVente": "%s",
                  "prixOrigine": %s,
                  "fraisOrigine": %s,
                  "fraisReels": %s,
                  "prixVente": %s,
                  "fraisVente": %s,
                  "travaux": "%s",
                  "montantTravaux": %s
                }
                """.formatted(
                    row.get("typeBien"),
                    row.get("usageBien"),
                    row.get("origineBien"),
                    row.get("dateOrigine"),
                    row.get("dateVente"),
                    row.get("prixOrigine"),
                    row.get("fraisOrigine"),
                    row.get("fraisReels"),
                    row.get("prixVente"),
                    row.get("fraisVente"),
                    row.get("travaux"),
                    row.get("montantTravaux")
            );

            MvcResult result = mockMvc.perform(
                    post("/api/v1/pvi/simulate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
            ).andReturn();

            String response = result.getResponse().getContentAsString();
            JSONObject jsonResponse = new JSONObject(response);

            actualResultsById.put(id, jsonResponse);
        }
    }

    // -------------------------------------------------
    // THEN : on compare les résultats réels aux attendus
    // -------------------------------------------------

    @Then("les résultats PVI doivent correspondre aux attentes")
    public void les_resultats_pvi_doivent_correspondre_aux_attentes() {
        for (String id : expectedById.keySet()) {

            Map<String, String> expected = expectedById.get(id);
            JSONObject actual = actualResultsById.get(id);

            // Champs numériques
            assertField(actual, expected, "plusValueBrute");
            assertField(actual, expected, "plusValueNette");
            assertField(actual, expected, "impotRevenu");
            assertField(actual, expected, "prelevementsSociaux");
            assertField(actual, expected, "taxePlusValue");
            assertField(actual, expected, "impotsTotaux");
            assertField(actual, expected, "netDispo");
            assertField(actual, expected, "pressionFiscale");

            // Champ texte (état)
            assertEquals(
                    expected.get("taxOutcomeState"),
                    actual.optString("taxOutcomeState", null),
                    "Mismatch on taxOutcomeState for scenario " + id
            );
        }
    }

    // -------------------------------------------------
    // Utilitaire pour comparer les champs numériques
    // -------------------------------------------------

    private void assertField(JSONObject json, Map<String, String> expected, String field) {
        String expectedVal = expected.get(field);

        // Permet de laisser la cellule vide si on ne veut pas vérifier ce champ
        if (expectedVal == null || expectedVal.isBlank()) {
            return;
        }

        double exp = Double.parseDouble(expectedVal);

        try {
            double act = json.getDouble(field);
            assertEquals(exp, act, 0.1,
                    "Mismatch on field '" + field + "'");
        } catch (JSONException e) {
            // On remonte en RuntimeException pour ne pas polluer toutes les signatures avec throws
            throw new RuntimeException("Error reading JSON field '" + field + "'", e);
        }
    }
}

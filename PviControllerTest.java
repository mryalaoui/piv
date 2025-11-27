package com.bnpparibas.dsibddf.fis.pvi.api;

import com.bnpparibas.dsibddf.fis.pvi.PviApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = PviApplication.class)
@AutoConfigureMockMvc
class PviControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_simulate_pvi_for_sample_request() throws Exception {
        String json = """
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

        mockMvc.perform(
                post("/api/v1/pvi/simulate")
                        .contentType("application/json")
                        .content(json)
        ).andExpect(status().isOk());
    }
}

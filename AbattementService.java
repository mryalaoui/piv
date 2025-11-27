package com.bnpparibas.dsibddf.fis.pvi.domain.service;

import com.bnpparibas.dsibddf.fis.pvi.domain.model.AbattementTaux;
import com.bnpparibas.dsibddf.fis.pvi.domain.model.AbattementYear;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class AbattementService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    private final Map<Integer, AbattementYear> abattementByYear = new TreeMap<>();

    @PostConstruct
    public void load() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("/pvi/abattement.json")) {
            JsonNode root = objectMapper.readTree(is);
            root.fields().forEachRemaining(entry -> {
                int year = Integer.parseInt(entry.getKey());
                JsonNode node = entry.getValue();

                JsonNode irNode = node.get("IR");
                JsonNode psNode = node.get("PS");

                AbattementTaux ir = new AbattementTaux(
                        toBigDecimal(irNode.get("tauxAnnuel")),
                        toBigDecimal(irNode.get("tauxAbattementGlobal"))
                );
                AbattementTaux ps = new AbattementTaux(
                        toBigDecimal(psNode.get("tauxAnnuel")),
                        toBigDecimal(psNode.get("tauxAbattementGlobal"))
                );

                abattementByYear.put(year, new AbattementYear(ir, ps));
            });
        }
    }

    public AbattementYear getAbattementFor(int detentionYears) {
        // Reproduction exacte du PHP : clamp sur min/max clés
        int minYear = abattementByYear.keySet().stream().min(Integer::compareTo).orElse(0);
        int maxYear = abattementByYear.keySet().stream().max(Integer::compareTo).orElse(0);

        int key;
        if (detentionYears < minYear) {
            key = minYear;
        } else if (detentionYears > maxYear) {
            key = maxYear;
        } else {
            key = detentionYears;
        }
        return abattementByYear.get(key);
    }

    private BigDecimal toBigDecimal(JsonNode n) {
        if (n == null || n.isNull() || (n.isTextual() && n.asText().isEmpty())) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(n.asText());
    }
}

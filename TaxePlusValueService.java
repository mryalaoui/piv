package com.bnpparibas.dsibddf.fis.pvi.domain.service;

import com.bnpparibas.dsibddf.fis.pvi.domain.model.TaxePlusValueBracket;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxePlusValueService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final List<TaxePlusValueBracket> brackets = new ArrayList<>();

    @PostConstruct
    public void load() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("/pvi/taxe-pv.json")) {
            JsonNode root = objectMapper.readTree(is);
            for (JsonNode node : root) {
                BigDecimal min = new BigDecimal(node.get("min").asText());
                BigDecimal max = null;
                if (node.hasNonNull("max") && !"-".equals(node.get("max").asText())) {
                    max = new BigDecimal(node.get("max").asText());
                }
                BigDecimal percent = new BigDecimal(node.get("%plusValue").asText());
                BigDecimal rapport = node.has("rapport") ? new BigDecimal(node.get("rapport").asText()) : BigDecimal.ZERO;

                brackets.add(new TaxePlusValueBracket(min, max, percent, rapport));
            }
        }
        brackets.sort(Comparator.comparing(TaxePlusValueBracket::min));
    }

    public BigDecimal computeTaxePlusValue(BigDecimal plusValueIR) {
        if (plusValueIR == null || plusValueIR.signum() <= 0) {
            return BigDecimal.ZERO;
        }

        for (TaxePlusValueBracket b : brackets) {
            boolean match;
            if (b.max() != null) {
                match = plusValueIR.compareTo(b.min()) >= 0 && plusValueIR.compareTo(b.max()) <= 0;
            } else {
                match = plusValueIR.compareTo(b.min()) >= 0;
            }
            if (match) {
                return computeBracketTax(b, plusValueIR);
            }
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal computeBracketTax(TaxePlusValueBracket b, BigDecimal plusValueIR) {
        // PHP: round((%plusValue/100 * plusValueIR) - ((max - plusValueIR) * rapport))
        BigDecimal percentTerm = b.pourcentagePlusValue()
                .divide(new BigDecimal("100"))
                .multiply(plusValueIR);

        BigDecimal rapportTerm = BigDecimal.ZERO;
        if (b.max() != null && b.rapport() != null) {
            rapportTerm = b.max()
                    .subtract(plusValueIR)
                    .multiply(b.rapport());
        }

        return percentTerm.subtract(rapportTerm)
                .setScale(0, RoundingMode.HALF_UP);
    }
}

package com.bnpparibas.dsibddf.fis.pvi.domain.service;

import com.bnpparibas.dsibddf.fis.pvi.domain.model.PviSimulationRequest;
import com.bnpparibas.dsibddf.fis.pvi.domain.model.PviSimulationResult;
import com.bnpparibas.dsibddf.fis.pvi.domain.model.Suggestion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuggestionEngine {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private int maxSuggestions = 4;
    private final List<Suggestion> suggestions = new ArrayList<>();

    @PostConstruct
    public void load() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("/pvi/suggestion.json")) {
            JsonNode root = objectMapper.readTree(is);
            if (root.has("show")) {
                maxSuggestions = root.get("show").asInt(4);
            }

            JsonNode sugRoot = root.get("suggestions");
            if (sugRoot == null || !sugRoot.isObject()) {
                return;
            }

            Iterator<String> fieldNames = sugRoot.fieldNames();
            while (fieldNames.hasNext()) {
                String category = fieldNames.next();
                JsonNode array = sugRoot.get(category);
                if (array != null && array.isArray()) {
                    for (JsonNode sNode : array) {
                        String value = sNode.get("value").asText();
                        String url = sNode.hasNonNull("url") ? sNode.get("url").asText() : null;
                        JsonNode condition = sNode.get("condition"); // peut être null

                        suggestions.add(new Suggestion(category, value, url, condition));
                    }
                }
            }
        }
    }

    public List<String> computeSuggestions(PviSimulationRequest req, PviSimulationResult result) {
        List<String> out = new ArrayList<>();

        for (Suggestion s : suggestions) {
            if (out.size() >= maxSuggestions) {
                break;
            }
            if (s.conditionNode() == null || evalCondition(s.conditionNode(), req, result)) {
                // On retourne seulement le texte, comme ton PHP (tu peux concaténer le lien si tu veux)
                out.add(s.value());
            }
        }

        return out;
    }

    // =======================
    //   Évaluation des conditions
    // =======================

    private boolean evalCondition(JsonNode node, PviSimulationRequest req, PviSimulationResult result) {
        if (node == null || node.isNull()) {
            return true;
        }

        JsonNode part1 = node.get("part1");
        JsonNode opNode = node.get("operator");
        JsonNode part2 = node.get("part2");

        if (part1 == null || opNode == null || part2 == null) {
            return true; // condition mal formée → on considère valide
        }

        String op = opNode.asText();

        // Cas composite : part1 est lui-même un objet avec part1/operator/part2
        if (part1.isObject() && part1.has("part1") && part1.has("operator")) {
            boolean left = evalCondition(part1, req, result);
            boolean right = evalCondition(part2, req, result);
            return switch (op) {
                case "||" -> left || right;
                case "&&" -> left && right;
                default -> false;
            };
        }

        // Cas simple : part1 = nom de variable (string)
        String varName = part1.asText();
        Object leftValue = resolveVariable(varName, req, result);

        // part2 peut être un nombre ou une chaîne
        String rightRaw = part2.asText();

        return switch (op) {
            case "==" -> compareEquals(leftValue, rightRaw);
            case ">" -> compareGreater(leftValue, rightRaw);
            case "<" -> compareLess(leftValue, rightRaw);
            default -> false;
        };
    }

    private Object resolveVariable(String name, PviSimulationRequest req, PviSimulationResult result) {
        // Mapping des noms utilisés dans suggestion.json
        return switch (name) {
            case "plusValueBrute" -> result.getPlusValueBrute();
            case "plusValueNette" -> result.getPlusValueNette();
            case "impots" -> result.getImpotsTotaux();
            case "detentionAnnee" -> BigDecimal.valueOf(result.getDetentionAnnee());
            case "usageBien" ->
                // On renvoie la string en kebab-case pour matcher "residence-principale" etc.
                    req.usageBien().name().toLowerCase().replace('_', '-');
            case "typeBien" ->
                    req.typeBien().name().toLowerCase(); // "appartement", "maison", "terrain"
            case "origineBien" ->
                    req.origineBien().name().toLowerCase(); // "achat", "donation", "succession"
            default -> null;
        };
    }

    private boolean compareEquals(Object left, String rightRaw) {
        if (left == null) return false;

        if (left instanceof BigDecimal bd) {
            try {
                BigDecimal right = new BigDecimal(rightRaw);
                return bd.compareTo(right) == 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        String leftStr = left.toString();
        return leftStr.equalsIgnoreCase(rightRaw);
    }

    private boolean compareGreater(Object left, String rightRaw) {
        if (!(left instanceof BigDecimal bd)) {
            return false;
        }
        try {
            BigDecimal right = new BigDecimal(rightRaw);
            return bd.compareTo(right) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean compareLess(Object left, String rightRaw) {
        if (!(left instanceof BigDecimal bd)) {
            return false;
        }
        try {
            BigDecimal right = new BigDecimal(rightRaw);
            return bd.compareTo(right) < 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

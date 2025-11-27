package com.bnpparibas.dsibddf.fis.pvi.domain.model;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Représente une suggestion du simulateur PVI,
 * chargée depuis suggestion.json.
 */
public record Suggestion(
        String category,      // "general", "financier", "juridique", "fiscal"
        String value,         // texte de la suggestion
        String url,           // lien éventuel (peut être null ou vide)
        JsonNode conditionNode // nœud JSON de la condition (peut être null)
) {}

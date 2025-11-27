package com.bnpparibas.dsibddf.fis.pvi.domain.model;

public enum TravauxFlag {
    OUI, NON;

    public static TravauxFlag fromString(String value) {
        if (value == null) return NON;
        return "oui".equalsIgnoreCase(value) ? OUI : NON;
    }
}

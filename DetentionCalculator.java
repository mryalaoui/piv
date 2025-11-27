package com.bnpparibas.dsibddf.fis.pvi.domain.service;

import java.time.LocalDate;
import java.time.Period;

public final class DetentionCalculator {

    private DetentionCalculator() {}

    public static int yearsBetween(LocalDate origine, LocalDate vente) {
        return Period.between(origine, vente).getYears();
    }
}

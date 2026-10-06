package com.lecomptoir.engine.model;

public enum DiscountType {
    NONE("Aucune remise"),
    DRINKS_THIRD_FREE("3e boisson offerte"),
    TEN_PERCENT_OVER_FIFTY("Remise 10 %"),
    FIVE_EUROS_OVER_FIFTY("Remise 5 €"),
    LOYALTY("Remise fidélité");

    private final String label;

    DiscountType(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }
}

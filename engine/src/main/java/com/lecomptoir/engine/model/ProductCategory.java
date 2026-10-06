package com.lecomptoir.engine.model;

public enum ProductCategory {
    FOODS(0.055),
    DRINKS(0.20),
    OTHERS(0.20);

    private final double vatRate;

    ProductCategory(double vatRate) {
        this.vatRate = vatRate;
    }

    public double getVatRate() { return vatRate; }
}

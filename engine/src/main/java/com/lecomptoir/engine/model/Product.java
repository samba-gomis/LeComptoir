package com.lecomptoir.engine.model;

public class Product {
    private final String reference;
    private final String name;
    private final double unitPrice;
    private final ProductCategory category;

    public Product(String reference, String name, double unitPrice, ProductCategory category) {
        this.reference = reference;
        this.name = name;
        this.unitPrice = unitPrice;
        this.category = category;
    }

    public String getReference() { return reference; }
    public String getName() { return name; }
    public double getUnitPrice() { return unitPrice; }
    public ProductCategory getCategory() { return category; }
}

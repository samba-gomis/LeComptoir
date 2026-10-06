package com.lecomptoir.engine.model;

public class CartLine {
    private final Product product;
    private final int quantity;

    public CartLine(Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }

    public double getLineTotal() { return product.getUnitPrice() * quantity; }
}

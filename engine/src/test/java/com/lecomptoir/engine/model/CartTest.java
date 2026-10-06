package com.lecomptoir.engine.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class CartTest {
    @Test
    void totalIsSumOfLineTotals() {
        Product product = new Product("REF001", "Kiwi", 1.50, ProductCategory.FOODS);
        Cart cart = new Cart();
        cart.addLine(new CartLine(product, 3));

        assertEquals(4.5, cart.getTotal(), 0.001);
    }
}

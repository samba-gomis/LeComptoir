package com.lecomptoir.engine.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class LoyaltyCardTest {

    @Test
    void negativeInitialPointsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new LoyaltyCard(-1));
    }

    @Test
    void recordDebitsUsedPointsAndCreditsEarnedPoints() {
        Product soap = new Product("REF030", "Savon", 10.00, ProductCategory.OTHERS);
        LoyaltyCard card = new LoyaltyCard(120);

        Cart cart = new Cart();
        cart.addLine(new CartLine(soap, 1));
        Receipt receipt = new Checkout().checkout(cart, card);

        card.record(receipt);

        assertEquals(26, card.getPoints());
    }

    @Test
    void pointsEarnedAreOnlyUsableOnNextOrder() {
        Product soap = new Product("REF030", "Savon", 10.00, ProductCategory.OTHERS);
        LoyaltyCard card = new LoyaltyCard(0);
        Checkout checkout = new Checkout();

        Cart firstCart = new Cart();
        firstCart.addLine(new CartLine(soap, 10));
        Receipt firstReceipt = checkout.checkout(firstCart, card);
        card.record(firstReceipt);

        Cart secondCart = new Cart();
        secondCart.addLine(new CartLine(soap, 1));
        Receipt secondReceipt = checkout.checkout(secondCart, card);
        card.record(secondReceipt);

        assertEquals(0, firstReceipt.getPointsUsed());
        assertEquals(108, firstReceipt.getPointsEarned());
        assertEquals(DiscountType.LOYALTY, secondReceipt.getDiscountType());
        assertEquals(100, secondReceipt.getPointsUsed());
        assertEquals(14, card.getPoints());
    }

    @Test
    void recordRejectsReceiptUsingMorePointsThanAvailable() {
        Product soap = new Product("REF030", "Savon", 10.00, ProductCategory.OTHERS);
        LoyaltyCard card = new LoyaltyCard(100);

        Cart cart = new Cart();
        cart.addLine(new CartLine(soap, 1));
        Receipt receipt = new Checkout().checkout(cart, card);
        card.record(receipt);

        assertThrows(IllegalStateException.class, () -> card.record(receipt));
        assertEquals(6, card.getPoints());
    }
}

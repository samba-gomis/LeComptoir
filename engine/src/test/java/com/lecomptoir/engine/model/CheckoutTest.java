package com.lecomptoir.engine.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CheckoutTest {

    @Test
    void noDiscountWhenTotalIsBelowThreshold() {
        Product product = new Product("REF001", "Kiwi", 1.50, ProductCategory.FOODS);
        Cart cart = new Cart();
        cart.addLine(new CartLine(product, 20));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(30.0, receipt.getTotalExclTax(), 0.001);
    }

    @Test
    void noDiscountWhenTotalEqualsThresholdExactly() {
        Product product = new Product("REF001", "Kiwi", 25.0, ProductCategory.FOODS);
        Cart cart = new Cart();
        cart.addLine(new CartLine(product, 2));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(50.0, receipt.getTotalExclTax(), 0.001);
    }

    @Test
    void tenPercentDiscountWhenTotalExceedsFifty() {
        Product product = new Product("REF001", "Kiwi", 20.0, ProductCategory.FOODS);
        Cart cart = new Cart();
        cart.addLine(new CartLine(product, 3));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(54.0, receipt.getTotalExclTax(), 0.001);
    }

    @Test
    void cheapestOfThreeDrinksIsFree() {
        Product water = new Product("REF003", "Eau", 1.00, ProductCategory.DRINKS);
        Product juice = new Product("REF004", "Jus", 1.50, ProductCategory.DRINKS);
        Product coca = new Product("REF002", "Cola 1L", 2.00, ProductCategory.DRINKS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(coca, 1));
        cart.addLine(new CartLine(water, 1));
        cart.addLine(new CartLine(juice, 1));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(3.50, receipt.getTotalExclTax(), 0.001);
    }

    @Test
    void noDrinksDiscountWithOnlyTwoItems() {
        Product coca = new Product("REF002", "Cola 1L", 2.00, ProductCategory.DRINKS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(coca, 2));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(4.00, receipt.getTotalExclTax(), 0.001);
    }

    @Test
    void sixDrinksGiveTwoFreeItems() {
        Product coca = new Product("REF002", "Cola 1L", 3.00, ProductCategory.DRINKS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(coca, 6));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(12.00, receipt.getTotalExclTax(), 0.001);
    }

    @Test
    void foodIsNeverAffectedByDrinksOffer() {
        Product fruit = new Product("REF001", "Kiwi", 5.00, ProductCategory.FOODS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(fruit, 3));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(15.00, receipt.getTotalExclTax(), 0.001);
    }

    @Test
    void fourDrinksOfDifferentPricesOfferOnlyOneItem() {
        Product a = new Product("REF010", "Boisson A", 3.00, ProductCategory.DRINKS);
        Product b = new Product("REF011", "Boisson B", 2.00, ProductCategory.DRINKS);
        Product c = new Product("REF012", "Boisson C", 1.50, ProductCategory.DRINKS);
        Product d = new Product("REF013", "Boisson D", 1.00, ProductCategory.DRINKS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(d, 1));
        cart.addLine(new CartLine(a, 1));
        cart.addLine(new CartLine(c, 1));
        cart.addLine(new CartLine(b, 1));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(6.00, receipt.getTotalExclTax(), 0.001);
    }

    @Test
    void tenPercentThresholdIsEvaluatedOnGrossTotal() {
        Product fruit = new Product("REF001", "Kiwi", 5.00, ProductCategory.FOODS);
        Product coca = new Product("REF002", "Cola 1L", 2.00, ProductCategory.DRINKS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(fruit, 9));
        cart.addLine(new CartLine(coca, 3));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(DiscountType.TEN_PERCENT_OVER_FIFTY, receipt.getDiscountType());
        assertEquals(45.90, receipt.getTotalExclTax(), 0.001);
    }

    @Test
    void onlyBestDiscountIsKeptWhenDrinksOfferAndTenPercentApply() {
        Product fruit = new Product("REF001", "Kiwi", 5.00, ProductCategory.FOODS);
        Product coca = new Product("REF002", "Cola 1L", 2.00, ProductCategory.DRINKS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(fruit, 10));
        cart.addLine(new CartLine(coca, 3));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(DiscountType.TEN_PERCENT_OVER_FIFTY, receipt.getDiscountType());
        assertEquals(5.60, receipt.getDiscountAmount(), 0.001);
        assertEquals(50.40, receipt.getTotalExclTax(), 0.001);
    }

    @Test
    void drinksOfferWinsOverTenPercentWhenItSavesMore() {
        Product champagne = new Product("REF040", "Champagne", 20.00, ProductCategory.DRINKS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(champagne, 3));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(DiscountType.DRINKS_THIRD_FREE, receipt.getDiscountType());
        assertEquals(40.00, receipt.getTotalExclTax(), 0.001);
        assertEquals(48.00, receipt.getTotalInclTax(), 0.001);
    }

    @Test
    void foodIsTaxedAtFivePointFivePercent() {
        Product rice = new Product("REF020", "Riz", 2.00, ProductCategory.FOODS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(rice, 5));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(10.00, receipt.getTotalExclTax(), 0.001);
        assertEquals(Set.of(0.055), receipt.getVatByRate().keySet());
        assertEquals(0.55, receipt.getVatByRate().get(0.055), 0.001);
        assertEquals(10.55, receipt.getTotalInclTax(), 0.001);
    }

    @Test
    void drinksAndOtherProductsShareTwentyPercentRate() {
        Product soap = new Product("REF030", "Savon", 4.00, ProductCategory.OTHERS);
        Product coca = new Product("REF002", "Cola 1L", 2.00, ProductCategory.DRINKS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(soap, 1));
        cart.addLine(new CartLine(coca, 2));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(8.00, receipt.getTotalExclTax(), 0.001);
        assertEquals(Set.of(0.20), receipt.getVatByRate().keySet());
        assertEquals(1.60, receipt.getVatByRate().get(0.20), 0.001);
        assertEquals(9.60, receipt.getTotalInclTax(), 0.001);
    }

    @Test
    void receiptDetailsVatForEachRate() {
        Product rice = new Product("REF020", "Riz", 2.00, ProductCategory.FOODS);
        Product soap = new Product("REF030", "Savon", 5.00, ProductCategory.OTHERS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(rice, 5));
        cart.addLine(new CartLine(soap, 1));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(15.00, receipt.getTotalExclTax(), 0.001);
        assertEquals(0.55, receipt.getVatByRate().get(0.055), 0.001);
        assertEquals(1.00, receipt.getVatByRate().get(0.20), 0.001);
        assertEquals(16.55, receipt.getTotalInclTax(), 0.001);
    }

    @Test
    void vatIsComputedAfterDrinksOffer() {
        Product coca = new Product("REF002", "Cola 1L", 2.00, ProductCategory.DRINKS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(coca, 3));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(4.00, receipt.getTotalExclTax(), 0.001);
        assertEquals(0.80, receipt.getVatByRate().get(0.20), 0.001);
        assertEquals(4.80, receipt.getTotalInclTax(), 0.001);
    }

    @Test
    void vatIsComputedAfterTenPercentDiscount() {
        Product rice = new Product("REF020", "Riz", 4.00, ProductCategory.FOODS);
        Product soap = new Product("REF030", "Savon", 5.00, ProductCategory.OTHERS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(rice, 10));
        cart.addLine(new CartLine(soap, 4));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(54.00, receipt.getTotalExclTax(), 0.001);
        assertEquals(1.98, receipt.getVatByRate().get(0.055), 0.001);
        assertEquals(3.60, receipt.getVatByRate().get(0.20), 0.001);
        assertEquals(59.58, receipt.getTotalInclTax(), 0.001);
    }

    @Test
    void thresholdIsEvaluatedOnTotalExclTax() {
        Product rice = new Product("REF020", "Riz", 6.00, ProductCategory.FOODS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(rice, 8));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(48.00, receipt.getTotalExclTax(), 0.001);
        assertEquals(50.64, receipt.getTotalInclTax(), 0.001);
    }

    @Test
    void vatIsRoundedToTheCent() {
        Product rice = new Product("REF020", "Riz", 1.99, ProductCategory.FOODS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(rice, 1));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(0.11, receipt.getVatByRate().get(0.055), 0.001);
        assertEquals(2.10, receipt.getTotalInclTax(), 0.001);
    }

    @Test
    void receiptIsNotAffectedByLaterCartChanges() {
        Product rice = new Product("REF020", "Riz", 2.00, ProductCategory.FOODS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(rice, 1));
        Receipt receipt = new Checkout().checkout(cart);

        cart.addLine(new CartLine(rice, 3));

        assertEquals(1, receipt.getLines().size());
    }

    @Test
    void loyaltyDiscountAppliesWhenCardHasHundredPoints() {
        Product soap = new Product("REF030", "Savon", 10.00, ProductCategory.OTHERS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(soap, 1));

        Receipt receipt = new Checkout().checkout(cart, new LoyaltyCard(100));

        assertEquals(DiscountType.LOYALTY, receipt.getDiscountType());
        assertEquals(5.00, receipt.getDiscountAmount(), 0.001);
        assertEquals(5.00, receipt.getTotalExclTax(), 0.001);
        assertEquals(6.00, receipt.getTotalInclTax(), 0.001);
        assertEquals(100, receipt.getPointsUsed());
        assertEquals(6, receipt.getPointsEarned());
    }

    @Test
    void drinksOfferWinsOverLoyalty() {
        Product juice = new Product("REF041", "Jus pressé", 10.00, ProductCategory.DRINKS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(juice, 3));

        Receipt receipt = new Checkout().checkout(cart, new LoyaltyCard(100));

        assertEquals(DiscountType.DRINKS_THIRD_FREE, receipt.getDiscountType());
        assertEquals(20.00, receipt.getTotalExclTax(), 0.001);
        assertEquals(0, receipt.getPointsUsed());
        assertEquals(24, receipt.getPointsEarned());
    }

    @Test
    void tenPercentWinsOverLoyalty() {
        Product soap = new Product("REF030", "Savon", 10.00, ProductCategory.OTHERS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(soap, 8));

        Receipt receipt = new Checkout().checkout(cart, new LoyaltyCard(100));

        assertEquals(DiscountType.TEN_PERCENT_OVER_FIFTY, receipt.getDiscountType());
        assertEquals(72.00, receipt.getTotalExclTax(), 0.001);
        assertEquals(86.40, receipt.getTotalInclTax(), 0.001);
        assertEquals(0, receipt.getPointsUsed());
        assertEquals(86, receipt.getPointsEarned());
    }

    @Test
    void loyaltyWinsOverTenPercentWithSeveralVouchers() {
        Product soap = new Product("REF030", "Savon", 10.00, ProductCategory.OTHERS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(soap, 6));

        Receipt receipt = new Checkout().checkout(cart, new LoyaltyCard(250));

        assertEquals(DiscountType.LOYALTY, receipt.getDiscountType());
        assertEquals(10.00, receipt.getDiscountAmount(), 0.001);
        assertEquals(50.00, receipt.getTotalExclTax(), 0.001);
        assertEquals(200, receipt.getPointsUsed());
        assertEquals(60, receipt.getPointsEarned());
    }

    @Test
    void tieKeepsTheOfferThatSparesLoyaltyPoints() {
        Product juice = new Product("REF041", "Jus pressé", 5.00, ProductCategory.DRINKS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(juice, 3));

        Receipt receipt = new Checkout().checkout(cart, new LoyaltyCard(100));

        assertEquals(DiscountType.DRINKS_THIRD_FREE, receipt.getDiscountType());
        assertEquals(10.00, receipt.getTotalExclTax(), 0.001);
        assertEquals(0, receipt.getPointsUsed());
    }

    @Test
    void noLoyaltyDiscountBelowHundredPoints() {
        Product soap = new Product("REF030", "Savon", 10.00, ProductCategory.OTHERS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(soap, 1));

        Receipt receipt = new Checkout().checkout(cart, new LoyaltyCard(99));

        assertEquals(DiscountType.NONE, receipt.getDiscountType());
        assertEquals(10.00, receipt.getTotalExclTax(), 0.001);
        assertEquals(0, receipt.getPointsUsed());
        assertEquals(12, receipt.getPointsEarned());
    }

    @Test
    void loyaltyDiscountIsCappedByCartAmount() {
        Product soap = new Product("REF030", "Savon", 12.00, ProductCategory.OTHERS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(soap, 1));

        Receipt receipt = new Checkout().checkout(cart, new LoyaltyCard(300));

        assertEquals(DiscountType.LOYALTY, receipt.getDiscountType());
        assertEquals(10.00, receipt.getDiscountAmount(), 0.001);
        assertEquals(2.00, receipt.getTotalExclTax(), 0.001);
        assertEquals(200, receipt.getPointsUsed());
    }

    @Test
    void pointsAreEarnedOnTotalInclTaxRoundedDown() {
        Product soap = new Product("REF030", "Savon", 10.40, ProductCategory.OTHERS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(soap, 1));

        Receipt receipt = new Checkout().checkout(cart, new LoyaltyCard(0));

        assertEquals(12.48, receipt.getTotalInclTax(), 0.001);
        assertEquals(12, receipt.getPointsEarned());
    }

    @Test
    void noLoyaltyPointsWithoutCard() {
        Product soap = new Product("REF030", "Savon", 10.00, ProductCategory.OTHERS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(soap, 1));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(0, receipt.getPointsUsed());
        assertEquals(0, receipt.getPointsEarned());
    }

    @Test
    void tenPercentWinsOverFixedFiveEurosWhenItSavesMore() {
        Product rice = new Product("REF020", "Riz", 10.00, ProductCategory.FOODS);

        Cart cart = new Cart();
        cart.addLine(new CartLine(rice, 6));

        Receipt receipt = new Checkout().checkout(cart);

        assertEquals(DiscountType.TEN_PERCENT_OVER_FIFTY, receipt.getDiscountType());
        assertEquals(6.00, receipt.getDiscountAmount(), 0.001);
        assertEquals(54.00, receipt.getTotalExclTax(), 0.001);
    }

    // @Test
    // void tieBetweenTenPercentAndFixedFiveEurosKeepsTenPercent() {
    //     Product rice = new Product("REF020", "Riz", 50.01, ProductCategory.FOODS);

    //     Cart cart = new Cart();
    //     cart.addLine(new CartLine(rice, 1));

    //     Receipt receipt = new Checkout().checkout(cart);

    //     assertEquals(DiscountType.TEN_PERCENT_OVER_FIFTY, receipt.getDiscountType());
    //     assertEquals(5.00, receipt.getDiscountAmount(), 0.001);
    //     assertEquals(45.01, receipt.getTotalExclTax(), 0.001);
    // }

    // @Test
    // void checkoutDoesNotChangeLoyaltyCard() {
    //     Product soap = new Product("REF030", "Savon", 10.00, ProductCategory.OTHERS);
    //     LoyaltyCard card = new LoyaltyCard(100);

    //     Cart cart = new Cart();
    //     cart.addLine(new CartLine(soap, 1));

    //     Checkout checkout = new Checkout();
    //     Receipt first = checkout.checkout(cart, card);
    //     Receipt second = checkout.checkout(cart, card);

    //     assertEquals(100, card.getPoints());
    //     assertEquals(100, first.getPointsUsed());
    //     assertEquals(100, second.getPointsUsed());
    // }
}

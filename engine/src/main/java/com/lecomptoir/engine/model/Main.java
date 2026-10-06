package com.lecomptoir.engine.model;

public class Main {
    public static void main(String[] args) {
        Product fruit = new Product("REF001", "Kiwi", 2.50, ProductCategory.FOODS);
        Product coca = new Product("REF002", "CocaCola 1.5L", 1.80, ProductCategory.DRINKS);
        Checkout checkout = new Checkout();

        Cart smallCart = new Cart();
        smallCart.addLine(new CartLine(fruit, 2));
        smallCart.addLine(new CartLine(coca, 2));

        System.out.println("Petit panier (aucune remise) :");
        System.out.println(checkout.checkout(smallCart).display());

        Cart drinksCart = new Cart();
        drinksCart.addLine(new CartLine(fruit, 2));
        drinksCart.addLine(new CartLine(coca, 3));

        System.out.println("Panier boissons (3e boisson offerte) :");
        System.out.println(checkout.checkout(drinksCart).display());

        Cart bigCart = new Cart();
        bigCart.addLine(new CartLine(fruit, 22));
        bigCart.addLine(new CartLine(coca, 2));

        System.out.println("Gros panier (remise 10 %) :");
        System.out.println(checkout.checkout(bigCart).display());

        LoyaltyCard card = new LoyaltyCard(120);
        Receipt loyaltyReceipt = checkout.checkout(smallCart, card);
        card.record(loyaltyReceipt);

        System.out.println("Petit panier avec carte fidélité (120 points) :");
        System.out.println(loyaltyReceipt.display());
        System.out.println("Solde de points après passage en caisse : " + card.getPoints());
    }
}

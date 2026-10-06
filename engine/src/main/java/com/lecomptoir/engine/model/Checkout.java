package com.lecomptoir.engine.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Checkout {
    private static final double DISCOUNT_THRESHOLD = 50.0;
    private static final double DISCOUNT_RATE = 0.10;
    private static final double FIXED_DISCOUNT_AMOUNT = 5.0;
    private static final int POINTS_PER_VOUCHER = 100;
    private static final double VOUCHER_VALUE = 5.0;

    public Receipt checkout(Cart cart) {
        return buildReceipt(cart, 0, false);
    }

    public Receipt checkout(Cart cart, LoyaltyCard card) {
        return buildReceipt(cart, card.getPoints(), true);
    }

    private Receipt buildReceipt(Cart cart, int availablePoints, boolean hasCard) {
        Map<Double, Double> grossBaseByRate = computeBaseByRate(cart);
        double grossTotal = roundToCents(sum(grossBaseByRate));

        List<DiscountOption> options = new ArrayList<>();
        options.add(new DiscountOption(DiscountType.NONE, roundBases(grossBaseByRate), 0));

        double drinksDiscount = computeDrinksThirdFreeDiscount(cart);
        if (drinksDiscount > 0) {
            options.add(new DiscountOption(DiscountType.DRINKS_THIRD_FREE,
                    subtractFromDrinksBase(grossBaseByRate, drinksDiscount), 0));
        }

        if (grossTotal > DISCOUNT_THRESHOLD) {
            options.add(new DiscountOption(DiscountType.TEN_PERCENT_OVER_FIFTY,
                    spreadAmount(grossBaseByRate, roundToCents(grossTotal * DISCOUNT_RATE)), 0));

            options.add(new DiscountOption(DiscountType.FIVE_EUROS_OVER_FIFTY,
                    spreadAmount(grossBaseByRate, FIXED_DISCOUNT_AMOUNT), 0));
        }

        int vouchers = Math.min(availablePoints / POINTS_PER_VOUCHER, (int) (grossTotal / VOUCHER_VALUE));
        if (vouchers > 0) {
            options.add(new DiscountOption(DiscountType.LOYALTY,
                    spreadAmount(grossBaseByRate, vouchers * VOUCHER_VALUE), vouchers * POINTS_PER_VOUCHER));
        }

        DiscountOption best = options.get(0);
        for (DiscountOption option : options) {
            if (computeTotalInclTax(option.baseByRate()) < computeTotalInclTax(best.baseByRate())) {
                best = option;
            }
        }

        double totalExclTax = roundToCents(sum(best.baseByRate()));
        double totalInclTax = computeTotalInclTax(best.baseByRate());
        double discountAmount = roundToCents(grossTotal - totalExclTax);
        int pointsEarned = hasCard ? (int) Math.floor(totalInclTax) : 0;

        return new Receipt(cart.getLines(), best.type(), discountAmount, totalExclTax,
                computeVatByRate(best.baseByRate()), totalInclTax, best.pointsUsed(), pointsEarned);
    }

    private Map<Double, Double> computeBaseByRate(Cart cart) {
        Map<Double, Double> baseByRate = new TreeMap<>();
        for (CartLine line : cart.getLines()) {
            baseByRate.merge(line.getProduct().getCategory().getVatRate(), line.getLineTotal(), Double::sum);
        }
        return baseByRate;
    }

    private double computeDrinksThirdFreeDiscount(Cart cart) {
        List<Double> drinksPrices = new ArrayList<>();

        for (CartLine line : cart.getLines()) {
            if (line.getProduct().getCategory() == ProductCategory.DRINKS) {
                for (int i = 0; i < line.getQuantity(); i++) {
                    drinksPrices.add(line.getProduct().getUnitPrice());
                }
            }
        }

        drinksPrices.sort(Collections.reverseOrder());

        double discount = 0;
        for (int i = 2; i < drinksPrices.size(); i += 3) {
            discount += drinksPrices.get(i);
        }

        return discount;
    }

    private Map<Double, Double> subtractFromDrinksBase(Map<Double, Double> baseByRate, double amount) {
        Map<Double, Double> result = new TreeMap<>(baseByRate);
        result.computeIfPresent(ProductCategory.DRINKS.getVatRate(), (rate, base) -> base - amount);
        return roundBases(result);
    }

    private Map<Double, Double> spreadAmount(Map<Double, Double> baseByRate, double amount) {
        double total = sum(baseByRate);
        double remaining = amount;
        int index = 0;
        Map<Double, Double> result = new TreeMap<>();
        for (Map.Entry<Double, Double> entry : baseByRate.entrySet()) {
            index++;
            double share = index == baseByRate.size() ? remaining : roundToCents(amount * entry.getValue() / total);
            remaining -= share;
            result.put(entry.getKey(), roundToCents(entry.getValue() - share));
        }
        return result;
    }

    private Map<Double, Double> roundBases(Map<Double, Double> baseByRate) {
        Map<Double, Double> result = new TreeMap<>();
        baseByRate.forEach((rate, base) -> result.put(rate, roundToCents(base)));
        return result;
    }

    private Map<Double, Double> computeVatByRate(Map<Double, Double> baseByRate) {
        Map<Double, Double> vatByRate = new TreeMap<>();
        baseByRate.forEach((rate, base) -> vatByRate.put(rate, roundToCents(base * rate)));
        return vatByRate;
    }

    private double computeTotalInclTax(Map<Double, Double> baseByRate) {
        return roundToCents(sum(baseByRate) + sum(computeVatByRate(baseByRate)));
    }

    private double sum(Map<Double, Double> amounts) {
        double total = 0;
        for (double amount : amounts.values()) {
            total += amount;
        }
        return total;
    }

    private double roundToCents(double amount) {
        return Math.round(amount * 100) / 100.0;
    }

    private record DiscountOption(DiscountType type, Map<Double, Double> baseByRate, int pointsUsed) {}
}

package com.lecomptoir.engine.model;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class Receipt {
    private final List<CartLine> lines;
    private final DiscountType discountType;
    private final double discountAmount;
    private final double totalExclTax;
    private final Map<Double, Double> vatByRate;
    private final double totalInclTax;
    private final int pointsUsed;
    private final int pointsEarned;

    public Receipt(List<CartLine> lines, DiscountType discountType, double discountAmount, double totalExclTax,
                   Map<Double, Double> vatByRate, double totalInclTax, int pointsUsed, int pointsEarned) {
        this.lines = List.copyOf(lines);
        this.discountType = discountType;
        this.discountAmount = discountAmount;
        this.totalExclTax = totalExclTax;
        this.vatByRate = Collections.unmodifiableMap(new TreeMap<>(vatByRate));
        this.totalInclTax = totalInclTax;
        this.pointsUsed = pointsUsed;
        this.pointsEarned = pointsEarned;
    }

    public List<CartLine> getLines() { return lines; }
    public DiscountType getDiscountType() { return discountType; }
    public double getDiscountAmount() { return discountAmount; }
    public double getTotalExclTax() { return totalExclTax; }
    public Map<Double, Double> getVatByRate() { return vatByRate; }
    public double getTotalInclTax() { return totalInclTax; }
    public int getPointsUsed() { return pointsUsed; }
    public int getPointsEarned() { return pointsEarned; }

    public String display() {
        StringBuilder sb = new StringBuilder();
        for (CartLine line : lines) {
            sb.append(String.format(Locale.FRANCE, "%-20s x%-3d %8.2f EUR%n",
                    line.getProduct().getName(), line.getQuantity(), line.getLineTotal()));
        }
        if (discountType != DiscountType.NONE) {
            sb.append(formatAmountLine(discountType.getLabel(), -discountAmount));
        }
        sb.append(formatAmountLine("Total HT", totalExclTax));
        for (Map.Entry<Double, Double> entry : vatByRate.entrySet()) {
            sb.append(formatAmountLine("TVA " + formatRate(entry.getKey()) + " %", entry.getValue()));
        }
        sb.append(formatAmountLine("Total TTC", totalInclTax));
        if (pointsUsed > 0 || pointsEarned > 0) {
            sb.append(formatPointsLine("Points utilisés", pointsUsed));
            sb.append(formatPointsLine("Points gagnés", pointsEarned));
        }
        return sb.toString();
    }

    private String formatAmountLine(String label, double amount) {
        return String.format(Locale.FRANCE, "%-25s %8.2f EUR%n", label, amount);
    }

    private String formatPointsLine(String label, int points) {
        return String.format(Locale.FRANCE, "%-25s %8d%n", label, points);
    }

    private String formatRate(double rate) {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.FRANCE);
        format.setMaximumFractionDigits(1);
        return format.format(rate * 100);
    }
}

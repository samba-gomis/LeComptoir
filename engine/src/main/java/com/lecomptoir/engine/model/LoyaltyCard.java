package com.lecomptoir.engine.model;

public class LoyaltyCard {
    private int points;

    public LoyaltyCard(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Points must not be negative");
        }
        this.points = points;
    }

    public int getPoints() { return points; }

    public void record(Receipt receipt) {
        if (receipt.getPointsUsed() > points) {
            throw new IllegalStateException("Not enough loyalty points");
        }
        points = points - receipt.getPointsUsed() + receipt.getPointsEarned();
    }
}

package com.lecomptoir.engine.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart {
    private final List<CartLine> lines = new ArrayList<>();

    public void addLine(CartLine line) {
        lines.add(line);
    }

    public List<CartLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public double getTotal() {
        double total = 0;
        for (CartLine line : lines) {
            total += line.getLineTotal();
        }
        return total;
    }
}

package com.example.pda.cart;

import com.example.pda.Models.Product;

import java.util.ArrayList;
import java.util.List;

public class Invoice {
    private final List<InvoiceLine> lines = new ArrayList<>();

    public void addProduct(Product product) {
        for (InvoiceLine line : lines) {
            if (line.product.equals(product)) {
                line.quantity++;
                return;
            }
        }
        lines.add(new InvoiceLine(product, 1));
    }

    public List<InvoiceLine> getLines() {
        return lines;
    }

    public double getTotalHT() {
        double total = 0;
        for (InvoiceLine line : lines) total += line.getLineTotalHT();
        return total;
    }

    public double getTotalTax() {
        double total = 0;
        for (InvoiceLine line : lines) total += line.getLineTax();
        return total;
    }

    public double getTotalTTC() {
        return getTotalHT() + getTotalTax();
    }
}

package com.example.pda.cart;

import com.example.pda.Models.Product;

public class InvoiceLine {
    public Product product;
    public int quantity;

    public InvoiceLine(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public double getLineTotalHT() {
        return product.getPrice() * quantity;
    }

    public double getLineTax() {
        return getLineTotalHT() * product.getTaxRate();
    }

    public double getLineTotalTTC() {
        return getLineTotalHT() + getLineTax();
    }

    @Override
    public String toString() {
        return product.getName() + " x" + quantity + " - TTC: " + String.format("%.2f €", getLineTotalTTC());
    }
}

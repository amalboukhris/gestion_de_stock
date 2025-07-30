package com.example.pda.Models;

public class Product {
    private int id;
    private String name;
    private double price;
    private double taxRate;
    private String reference; // EPC ou code produit
    private int reorderThreshold;

    // Getters et setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getTaxRate() { return taxRate; }
    public void setTaxRate(double taxRate) { this.taxRate = taxRate; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public int getReorderThreshold() { return reorderThreshold; }
    public void setReorderThreshold(int reorderThreshold) { this.reorderThreshold = reorderThreshold; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product p = (Product) o;
        return reference.equals(p.reference);
    }

    @Override
    public int hashCode() {
        return reference.hashCode();
    }
}

package com.example.pda.Models;

public class InvoiceLine {
    private int quantity;
    private float unitPrice;
    private float taxRate;
    private float taxAmount;
    private float subTotal;
    private float total;

    public int getQuantity() { return quantity; }
    public float getUnitPrice() { return unitPrice; }
    public float getTaxRate() { return taxRate; }
    public float getTaxAmount() { return taxAmount; }
    public float getSubTotal() { return subTotal; }
    public float getTotal() { return total; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setUnitPrice(float unitPrice) { this.unitPrice = unitPrice; }
    public void setTaxRate(float taxRate) { this.taxRate = taxRate; }
    public void setTaxAmount(float taxAmount) { this.taxAmount = taxAmount; }
    public void setSubTotal(float subTotal) { this.subTotal = subTotal; }
    public void setTotal(float total) { this.total = total; }
}

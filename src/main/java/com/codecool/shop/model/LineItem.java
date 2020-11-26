package com.codecool.shop.model;

import java.util.Currency;

public class LineItem extends BaseModel {
    private int productId;
    private int quantity;
    private double unitPrice;
    private Currency currency;
    private double total;

    public LineItem(Product product) {
        super(product.name);
        this.productId = product.getId();
        this.name = product.getName();
        this.quantity = 1;
        this.unitPrice = product.getDefaultPrice();
        this.currency = product.getDefaultCurrency();
    }

    public LineItem(String name, int id, int quantity, double unitPrice, String currency) {
        super(name);
        this.productId = id;
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.currency = Currency.getInstance(currency);
        this.total = getSubTotal();
    }

    public double getTotal() {
        return total;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int id) {
        this.productId = id;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getSubTotal() {
        return this.quantity * this.unitPrice;
    }

    public String getSubTotalWithCurrency() {
        return (this.quantity * this.unitPrice) + this.currency.toString();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void incrementQuantity() {
        this.quantity++;
    }

    public void decrementQuantity() {
        this.quantity--;
    }
}
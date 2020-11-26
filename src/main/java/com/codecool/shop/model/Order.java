package com.codecool.shop.model;

import java.sql.Timestamp;

public class Order {

    private int orderId;
    private Timestamp orderDate;
    private double total;


    public Order(int orderId, Timestamp orderDate, double total) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.total = total;
    }

    public double getTotal() {
        return total;
    }

    public int getOrderId() {
        return orderId;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }
}

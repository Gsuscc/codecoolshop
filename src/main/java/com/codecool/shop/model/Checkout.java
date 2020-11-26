package com.codecool.shop.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Checkout {

    @SerializedName("confirmationData")
    @Expose
    private BillingData confirmationData;
    @SerializedName("paymentData")
    @Expose
    private PaymentData paymentData;
    @SerializedName("shippingData")
    @Expose
    private ShippingData shippingData;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BillingData getBillingData() {
        return confirmationData;
    }

    public void setBillingData(BillingData confirmationData) {
        this.confirmationData = confirmationData;
    }

    public PaymentData getPaymentData() {
        return paymentData;
    }

    public void setPaymentData(PaymentData paymentData) {
        this.paymentData = paymentData;
    }

    public ShippingData getShippingData() {
        return shippingData;
    }

    public void setShippingData(ShippingData shippingData) {
        this.shippingData = shippingData;
    }

}
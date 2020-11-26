package com.codecool.shop.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentData {

    @SerializedName("paymentMethod")
    @Expose
    private String paymentMethod;
    @SerializedName("cc-name")
    @Expose
    private String ccName;
    @SerializedName("cc-number")
    @Expose
    private String ccNumber;
    @SerializedName("cc-expiration")
    @Expose
    private String ccExpiration;
    @SerializedName("cc-cvv")
    @Expose
    private String ccCvv;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCcName() {
        return ccName;
    }

    public void setCcName(String ccName) {
        this.ccName = ccName;
    }

    public String getCcNumber() {
        return ccNumber;
    }

    public void setCcNumber(String ccNumber) {
        this.ccNumber = ccNumber;
    }

    public String getCcExpiration() {
        return ccExpiration;
    }

    public void setCcExpiration(String ccExpiration) {
        this.ccExpiration = ccExpiration;
    }

    public String getCcCvv() {
        return ccCvv;
    }

    public void setCcCvv(String ccCvv) {
        this.ccCvv = ccCvv;
    }

}

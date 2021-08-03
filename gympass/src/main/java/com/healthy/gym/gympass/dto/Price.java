package com.healthy.gym.gympass.dto;

import java.util.Objects;

public class Price {
    private double amount;
    private String currency;
    private String period;

    public Price() {
    }

    public Price(double amount, String currency, String period) {
        this.amount = amount;
        this.currency = currency;
        this.period = period;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return Double.compare(price.amount, amount) == 0
                && Objects.equals(currency, price.currency)
                && Objects.equals(period, price.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency, period);
    }

    @Override
    public String toString() {
        return "Price{" +
                "amount=" + amount +
                ", currency='" + currency + '\'' +
                ", period='" + period + '\'' +
                '}';
    }
}

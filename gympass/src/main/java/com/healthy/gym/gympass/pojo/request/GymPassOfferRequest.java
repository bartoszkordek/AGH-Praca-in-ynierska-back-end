package com.healthy.gym.gympass.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.DefaultValue;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GymPassOfferRequest {

    @NotNull(message = "{field.required}")
    @Size(min = 2, max = 20, message = "{field.name.failure}")
    private String title;

    @Size(max = 60, message = "{field.subheader.failure}")
    private String subheader;

    @NotNull(message = "{field.required}")
    private double amount;

    @DefaultValue("zł")
    private String currency;

    @NotNull(message = "{field.required}")
    @Size(min = 2, max = 20, message = "{field.period.failure}")
    private String period;

    @NotNull(message = "{field.required}")
    private boolean isPremium;

    @Size(min = 2, max = 60, message = "{field.synopsis.failure}")
    private String synopsis;

    @Size(min = 0, max = 20, message = "{field.features.failure}")
    private List<String> features;

    private boolean isTemporaryPass;

    private int quantity;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubheader() {
        return subheader;
    }

    public void setSubheader(String subheader) {
        this.subheader = subheader;
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

    public boolean isPremium() {
        return isPremium;
    }

    public void setIsPremium(boolean premium) {
        this.isPremium = premium;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public boolean isTemporaryPass() {
        return isTemporaryPass;
    }

    public void setIsTemporaryPass(boolean temporaryPass) {
        this.isTemporaryPass = temporaryPass;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "GymPassOfferRequest{" +
                "title='" + title + '\'' +
                ", subheader='" + subheader + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", period='" + period + '\'' +
                ", isPremium=" + isPremium +
                ", synopsis='" + synopsis + '\'' +
                ", features=" + features +
                ", isTemporaryPass=" + isTemporaryPass +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GymPassOfferRequest that = (GymPassOfferRequest) o;
        return Double.compare(that.amount, amount) == 0
                && isPremium == that.isPremium
                && isTemporaryPass == that.isTemporaryPass
                && quantity == that.quantity
                && Objects.equals(title, that.title)
                && Objects.equals(subheader, that.subheader)
                && Objects.equals(currency, that.currency)
                && Objects.equals(period, that.period)
                && Objects.equals(synopsis, that.synopsis)
                && Objects.equals(features, that.features);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                title,
                subheader,
                amount,
                currency,
                period,
                isPremium,
                synopsis,
                features,
                isTemporaryPass,
                quantity
        );
    }
}

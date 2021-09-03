package com.healthy.gym.gympass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.gympass.shared.Price;

import java.util.Objects;

public class SimpleGymPassDTO {

    @JsonProperty("gymPassOfferId")
    private String documentId;
    private String title;
    private Price price;
    private boolean premium;
    private boolean temporaryPass;

    public SimpleGymPassDTO() {
    }

    public SimpleGymPassDTO(
            String documentId,
            String title,
            Price price,
            boolean premium
    ) {
        this.documentId = documentId;
        this.title = title;
        this.price = price;
        this.premium = premium;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public boolean isTemporaryPass() {
        return temporaryPass;
    }

    public void setTemporaryPass(boolean temporaryPass) {
        this.temporaryPass = temporaryPass;
    }

    @Override
    public String toString() {
        return "SimpleGymPassDTO{" +
                "documentId='" + documentId + '\'' +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", isPremium=" + premium +
                ", isTemporaryPass=" + temporaryPass +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleGymPassDTO that = (SimpleGymPassDTO) o;
        return premium == that.premium
                && temporaryPass == that.temporaryPass
                && Objects.equals(documentId, that.documentId)
                && Objects.equals(title, that.title)
                && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId, title, price, premium, temporaryPass);
    }
}

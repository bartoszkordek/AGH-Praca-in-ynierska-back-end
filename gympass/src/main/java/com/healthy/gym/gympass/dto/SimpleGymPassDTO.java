package com.healthy.gym.gympass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.gympass.shared.Price;

import java.util.Objects;

public class SimpleGymPassDTO {

    @JsonProperty("gymPassOfferId")
    private String documentId;
    private String title;
    private Price price;
    private boolean isPremium;

    public SimpleGymPassDTO(){}

    public SimpleGymPassDTO(
            String documentId,
            String title,
            Price price,
            boolean isPremium
    ){
        this.documentId = documentId;
        this.title = title;
        this.price = price;
        this.isPremium = isPremium;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getTitle() {
        return title;
    }

    public Price getPrice() {
        return price;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    @Override
    public String toString() {
        return "SimpleGymPassDTO{" +
                "documentId='" + documentId + '\'' +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", isPremium=" + isPremium +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleGymPassDTO that = (SimpleGymPassDTO) o;
        return isPremium == that.isPremium &&
                Objects.equals(documentId, that.documentId) &&
                Objects.equals(title, that.title) &&
                Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                documentId,
                title,
                price,
                isPremium
        );
    }
}

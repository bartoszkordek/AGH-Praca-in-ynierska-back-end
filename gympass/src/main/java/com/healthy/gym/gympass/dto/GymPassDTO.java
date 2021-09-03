package com.healthy.gym.gympass.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.gympass.shared.Description;
import com.healthy.gym.gympass.shared.Price;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GymPassDTO {

    private String documentId;
    private String title;
    private String subheader;
    private Price price;
    @JsonProperty("isPremium")
    private boolean premium;
    private Description description;
    private boolean isTemporaryPass;
    private int quantity;

    public GymPassDTO() {
    }

    public GymPassDTO(
            String documentId,
            String title,
            String subheader,
            Price price,
            boolean premium,
            Description description
    ) {
        this.documentId = documentId;
        this.title = title;
        this.subheader = subheader;
        this.price = price;
        this.premium = premium;
        this.description = description;
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

    public String getSubheader() {
        return subheader;
    }

    public void setSubheader(String subheader) {
        this.subheader = subheader;
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

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public boolean isTemporaryPass() {
        return isTemporaryPass;
    }

    public void setTemporaryPass(boolean temporaryPass) {
        isTemporaryPass = temporaryPass;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "GymPassDTO{" +
                "documentId='" + documentId + '\'' +
                ", title='" + title + '\'' +
                ", subheader='" + subheader + '\'' +
                ", price=" + price +
                ", premium=" + premium +
                ", description=" + description +
                ", isTemporaryPass=" + isTemporaryPass +
                ", quantity=" + quantity +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GymPassDTO that = (GymPassDTO) o;
        return premium == that.premium
                && isTemporaryPass == that.isTemporaryPass
                && quantity == that.quantity
                && Objects.equals(documentId, that.documentId)
                && Objects.equals(title, that.title)
                && Objects.equals(subheader, that.subheader)
                && Objects.equals(price, that.price)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                documentId,
                title,
                subheader,
                price,
                premium,
                description,
                isTemporaryPass,
                quantity
        );
    }
}

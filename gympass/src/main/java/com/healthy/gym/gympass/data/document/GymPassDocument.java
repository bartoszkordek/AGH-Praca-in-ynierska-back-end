package com.healthy.gym.gympass.data.document;

import com.healthy.gym.gympass.shared.Description;
import com.healthy.gym.gympass.shared.Price;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "gymPasses")
public class GymPassDocument {
    @Id
    private String id;
    private String documentId;
    private String title;
    private String subheader;
    private Price price;
    private boolean premium;
    private Description description;
    private boolean temporaryPass;
    private int quantity;

    public GymPassDocument() {
        //empty constructor required spring data mapper
    }

    public GymPassDocument(
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public String getSubheader() {
        return subheader;
    }

    public void setSubheader(String subheader) {
        this.subheader = subheader;
    }

    public boolean isTemporaryPass() {
        return temporaryPass;
    }

    public void setTemporaryPass(boolean temporaryPass) {
        this.temporaryPass = temporaryPass;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GymPassDocument that = (GymPassDocument) o;
        return premium == that.premium
                && temporaryPass == that.temporaryPass
                && quantity == that.quantity
                && Objects.equals(id, that.id)
                && Objects.equals(documentId, that.documentId)
                && Objects.equals(title, that.title)
                && Objects.equals(subheader, that.subheader)
                && Objects.equals(price, that.price)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                documentId,
                title,
                subheader,
                price,
                premium,
                description,
                temporaryPass,
                quantity
        );
    }

    @Override
    public String toString() {
        return "GymPassDocument{" +
                "id='" + id + '\'' +
                ", documentId='" + documentId + '\'' +
                ", title='" + title + '\'' +
                ", subheader='" + subheader + '\'' +
                ", price=" + price +
                ", isPremium=" + premium +
                ", description=" + description +
                ", isTemporaryPass=" + temporaryPass +
                ", quantity=" + quantity +
                '}';
    }
}

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
    private boolean isPremium;
    private Description description;
    private boolean isTemporaryPass;
    private int quantity;

    public GymPassDocument() {
        //empty constructor required spring data mapper
    }

    public GymPassDocument(
            String documentId,
            String title,
            String subheader,
            Price price,
            boolean isPremium,
            Description description
    ) {
        this.documentId = documentId;
        this.title = title;
        this.subheader = subheader;
        this.price = price;
        this.isPremium = isPremium;
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
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GymPassDocument that = (GymPassDocument) o;
        return isPremium == that.isPremium
                && isTemporaryPass == that.isTemporaryPass
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
                isPremium,
                description,
                isTemporaryPass,
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
                ", isPremium=" + isPremium +
                ", description=" + description +
                ", isTemporaryPass=" + isTemporaryPass +
                ", quantity=" + quantity +
                '}';
    }
}

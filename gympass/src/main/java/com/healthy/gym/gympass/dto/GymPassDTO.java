package com.healthy.gym.gympass.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.healthy.gym.gympass.shared.Description;
import com.healthy.gym.gympass.shared.Price;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GymPassDTO {

    private String documentId;
    private String title;
    private String subheader;
    private Price price;
    private boolean isPremium;
    private Description description;

    public GymPassDTO(){}

    public GymPassDTO(
            String documentId,
            String title,
            String subheader,
            Price price,
            boolean isPremium,
            Description description
    ){
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

    public String getTitle() {
        return title;
    }

    public String getSubheader() {
        return subheader;
    }

    public Price getPrice() {
        return price;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public Description getDescription() {
        return description;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubheader(String subheader) {
        this.subheader = subheader;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public void setDescription(Description description) {
        this.description = description;
    }
}

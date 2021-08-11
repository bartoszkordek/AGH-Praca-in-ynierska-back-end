package com.healthy.gym.gympass.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchasedGymPassDTO {

    private String purchasedGymPassDocumentId;
    private SimpleGymPassDTO gymPassOffer;
    private BasicUserInfoDTO user;
    private LocalDateTime purchaseDateTime;
    private LocalDate startDate;
    private LocalDate endDate;
    private int entries;
    private LocalDate suspensionDate;


    public PurchasedGymPassDTO(){}

    public PurchasedGymPassDTO(
            String purchasedGymPassDocumentId,
            SimpleGymPassDTO gymPassOffer,
            BasicUserInfoDTO user,
            LocalDateTime purchaseDateTime,
            LocalDate startDate,
            LocalDate endDate,
            int entries
    ){
        this.purchasedGymPassDocumentId = purchasedGymPassDocumentId;
        this.gymPassOffer = gymPassOffer;
        this.user = user;
        this.purchaseDateTime = purchaseDateTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.entries = entries;
    }

    public PurchasedGymPassDTO(
            String purchasedGymPassDocumentId,
            SimpleGymPassDTO gymPassOffer,
            BasicUserInfoDTO user,
            LocalDateTime purchaseDateTime,
            LocalDate startDate,
            LocalDate endDate,
            int entries,
            LocalDate suspensionDate
    ){
        this.purchasedGymPassDocumentId = purchasedGymPassDocumentId;
        this.gymPassOffer = gymPassOffer;
        this.user = user;
        this.purchaseDateTime = purchaseDateTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.entries = entries;
        this.suspensionDate = suspensionDate;
    }

    public String getPurchasedGymPassDocumentId() {
        return purchasedGymPassDocumentId;
    }

    public SimpleGymPassDTO getGymPassOffer() {
        return gymPassOffer;
    }

    public BasicUserInfoDTO getUser() {
        return user;
    }

    public LocalDateTime getPurchaseDateTime() {
        return purchaseDateTime;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getEntries() {
        return entries;
    }

    public LocalDate getSuspensionDate() {
        return suspensionDate;
    }

    public void setPurchasedGymPassDocumentId(String purchasedGymPassDocumentId) {
        this.purchasedGymPassDocumentId = purchasedGymPassDocumentId;
    }

    public void setGymPassOffer(SimpleGymPassDTO gymPassOffer) {
        this.gymPassOffer = gymPassOffer;
    }

    public void setUser(BasicUserInfoDTO user) {
        this.user = user;
    }

    public void setPurchaseDateTime(LocalDateTime purchaseDateTime) {
        this.purchaseDateTime = purchaseDateTime;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setEntries(int entries) {
        this.entries = entries;
    }

    public void setSuspensionDate(LocalDate suspensionDate) {
        this.suspensionDate = suspensionDate;
    }

    @Override
    public String toString() {
        return "PurchasedGymPassDTO{" +
                "purchasedGymPassDocumentId='" + purchasedGymPassDocumentId + '\'' +
                ", gymPassOffer=" + gymPassOffer +
                ", user=" + user +
                ", purchaseDateTime=" + purchaseDateTime +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", entries=" + entries +
                ", suspensionDate=" + suspensionDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchasedGymPassDTO that = (PurchasedGymPassDTO) o;
        return entries == that.entries &&
                Objects.equals(purchasedGymPassDocumentId, that.purchasedGymPassDocumentId) &&
                Objects.equals(gymPassOffer, that.gymPassOffer) &&
                Objects.equals(user, that.user) &&
                Objects.equals(purchaseDateTime, that.purchaseDateTime) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(suspensionDate, that.suspensionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                purchasedGymPassDocumentId,
                gymPassOffer,
                user,
                purchaseDateTime,
                startDate,
                endDate,
                entries,
                suspensionDate
        );
    }
}

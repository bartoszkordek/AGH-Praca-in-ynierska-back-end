package com.healthy.gym.gympass.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchasedGymPassDTO {

    private SimpleGymPassDTO gymPassOffer;
    private BasicUserInfoDTO user;
    private LocalDateTime purchaseDateAndTime;
    private LocalDate startDate;
    private LocalDate endDate;
    private int entries;
    private LocalDate suspensionDate;


    public PurchasedGymPassDTO(){}

    public PurchasedGymPassDTO(
            SimpleGymPassDTO gymPassOffer,
            BasicUserInfoDTO user,
            LocalDateTime purchaseDateAndTime,
            LocalDate startDate,
            LocalDate endDate,
            int entries
    ){
        this.gymPassOffer = gymPassOffer;
        this.user = user;
        this.purchaseDateAndTime = purchaseDateAndTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.entries = entries;
    }

    public PurchasedGymPassDTO(
            SimpleGymPassDTO gymPassOffer,
            BasicUserInfoDTO user,
            LocalDateTime purchaseDateAndTime,
            LocalDate startDate,
            LocalDate endDate,
            int entries,
            LocalDate suspensionDate
    ){
        this.gymPassOffer = gymPassOffer;
        this.user = user;
        this.purchaseDateAndTime = purchaseDateAndTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.entries = entries;
        this.suspensionDate = suspensionDate;
    }

    public SimpleGymPassDTO getGymPassOffer() {
        return gymPassOffer;
    }

    public BasicUserInfoDTO getUser() {
        return user;
    }

    public LocalDateTime getPurchaseDateAndTime() {
        return purchaseDateAndTime;
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

    public void setGymPassOffer(SimpleGymPassDTO gymPassOffer) {
        this.gymPassOffer = gymPassOffer;
    }

    public void setUser(BasicUserInfoDTO user) {
        this.user = user;
    }

    public void setPurchaseDateAndTime(LocalDateTime purchaseDateAndTime) {
        this.purchaseDateAndTime = purchaseDateAndTime;
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
                "gymPassOffer=" + gymPassOffer +
                ", user=" + user +
                ", purchaseDateAndTime=" + purchaseDateAndTime +
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
                Objects.equals(gymPassOffer, that.gymPassOffer) &&
                Objects.equals(user, that.user) &&
                Objects.equals(purchaseDateAndTime, that.purchaseDateAndTime) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(suspensionDate, that.suspensionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                gymPassOffer,
                user,
                purchaseDateAndTime,
                startDate,
                endDate,
                entries,
                suspensionDate
        );
    }
}

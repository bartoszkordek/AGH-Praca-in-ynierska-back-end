package com.healthy.gym.gympass.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchasedGymPassDTO {

    private SimpleGymPassDTO gymPassOffer;
    private BasicUserInfoDTO user;
    private String purchaseDateAndTime;
    private String startDate;
    private String endDate;
    private int entries;
    private String suspensionDate;


    public PurchasedGymPassDTO(){}

    public PurchasedGymPassDTO(
            SimpleGymPassDTO gymPassOffer,
            BasicUserInfoDTO user,
            String purchaseDateAndTime,
            String startDate,
            String endDate,
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
            String purchaseDateAndTime,
            String startDate,
            String endDate,
            int entries,
            String suspensionDate
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

    public String getPurchaseDateAndTime() {
        return purchaseDateAndTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getEntries() {
        return entries;
    }

    public String getSuspensionDate() {
        return suspensionDate;
    }

    public void setGymPassOffer(SimpleGymPassDTO gymPassOffer) {
        this.gymPassOffer = gymPassOffer;
    }

    public void setUser(BasicUserInfoDTO user) {
        this.user = user;
    }

    public void setPurchaseDateAndTime(String purchaseDateAndTime) {
        this.purchaseDateAndTime = purchaseDateAndTime;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setEntries(int entries) {
        this.entries = entries;
    }

    public void setSuspensionDate(String suspensionDate) {
        this.suspensionDate = suspensionDate;
    }

}

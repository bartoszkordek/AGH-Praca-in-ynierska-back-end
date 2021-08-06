package com.healthy.gym.gympass.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.healthy.gym.gympass.validation.ValidDateFormat;
import com.healthy.gym.gympass.validation.ValidIDFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchasedGymPassRequest {

    @NotNull(message = "{field.required}")
    @ValidIDFormat
    private String gymPassOfferId;

    @NotNull(message = "{field.required}")
    @ValidIDFormat
    private String userId;

    @NotNull(message = "{field.required}")
    @DateTimeFormat
    private String purchaseDateAndTime;

    @NotNull(message = "{field.required}")
    @ValidDateFormat
    private String startDate;

    @NotNull(message = "{field.required}")
    @ValidDateFormat
    private String endDate;

    @NotNull(message = "{field.required}")
    private int entries;

    @ValidDateFormat
    private String suspensionDate;


    public String getGymPassOfferId() {
        return gymPassOfferId;
    }

    public String getUserId() {
        return userId;
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

    public void setGymPassOfferId(String gymPassOfferId) {
        this.gymPassOfferId = gymPassOfferId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    @Override
    public String toString() {
        return "PurchasedGymPassRequest{" +
                "gymPassOfferId='" + gymPassOfferId + '\'' +
                ", userId='" + userId + '\'' +
                ", purchaseDateAndTime='" + purchaseDateAndTime + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", entries=" + entries +
                ", suspensionDate='" + suspensionDate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchasedGymPassRequest that = (PurchasedGymPassRequest) o;
        return entries == that.entries &&
                Objects.equals(gymPassOfferId, that.gymPassOfferId) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(purchaseDateAndTime, that.purchaseDateAndTime) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(suspensionDate, that.suspensionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                gymPassOfferId,
                userId,
                purchaseDateAndTime,
                startDate,
                endDate,
                entries,
                suspensionDate
        );
    }
}

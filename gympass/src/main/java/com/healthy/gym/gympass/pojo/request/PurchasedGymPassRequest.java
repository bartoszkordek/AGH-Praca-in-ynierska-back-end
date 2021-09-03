package com.healthy.gym.gympass.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.healthy.gym.gympass.validation.ValidDateFormat;
import com.healthy.gym.gympass.validation.ValidIDFormat;

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
    @ValidDateFormat
    private String startDate;

    public String getGymPassOfferId() {
        return gymPassOfferId;
    }

    public void setGymPassOfferId(String gymPassOfferId) {
        this.gymPassOfferId = gymPassOfferId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "PurchasedGymPassRequest{" +
                "gymPassOfferId='" + gymPassOfferId + '\'' +
                ", userId='" + userId + '\'' +
                ", startDate='" + startDate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchasedGymPassRequest that = (PurchasedGymPassRequest) o;
        return Objects.equals(gymPassOfferId, that.gymPassOfferId)
                && Objects.equals(userId, that.userId)
                && Objects.equals(startDate, that.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gymPassOfferId, userId, startDate);
    }
}

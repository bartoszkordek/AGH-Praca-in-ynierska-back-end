package com.healthy.gym.gympass.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchasedGymPassStatusValidationResultDTO {

    private boolean valid;
    private String endDate;
    private String suspensionDate;
    private int entries;

    public PurchasedGymPassStatusValidationResultDTO(){}

    //for time valid gympass
    public PurchasedGymPassStatusValidationResultDTO(
            boolean valid,
            String endDate,
            String suspensionDate
    ){
        this.valid = valid;
        this.endDate = endDate;
        this.suspensionDate = suspensionDate;
    }

    //for entries valid gympass
    public PurchasedGymPassStatusValidationResultDTO(
            boolean valid,
            String suspensionDate,
            int entries
    ){
        this.valid = valid;
        this.suspensionDate = suspensionDate;
        this.entries = entries;
    }

    public boolean isValid() {
        return valid;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getSuspensionDate() {
        return suspensionDate;
    }

    public int getEntries() {
        return entries;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setSuspensionDate(String suspensionDate) {
        this.suspensionDate = suspensionDate;
    }

    public void setEntries(int entries) {
        this.entries = entries;
    }

    @Override
    public String toString() {
        return "PurchasedGymPassStatusValidationResultDTO{" +
                "valid=" + valid +
                ", endDate='" + endDate + '\'' +
                ", suspensionDate='" + suspensionDate + '\'' +
                ", entries=" + entries +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchasedGymPassStatusValidationResultDTO that = (PurchasedGymPassStatusValidationResultDTO) o;
        return valid == that.valid &&
                entries == that.entries &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(suspensionDate, that.suspensionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                valid,
                endDate,
                suspensionDate,
                entries
        );
    }
}

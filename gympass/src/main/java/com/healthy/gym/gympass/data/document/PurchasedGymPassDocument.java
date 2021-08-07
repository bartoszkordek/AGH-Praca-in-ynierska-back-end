package com.healthy.gym.gympass.data.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Document(collection = "purchasedGymPasses")
public class PurchasedGymPassDocument {

    @Id
    private String id;
    @DBRef
    private GymPassDocument gymPassDocument;
    @DBRef
    private UserDocument userDocument;
    private LocalDateTime purchaseDateAndTime;
    private LocalDate startDate;
    private LocalDate endDate;
    private int entries;
    private LocalDate suspensionDate;

    public PurchasedGymPassDocument(){
        //empty constructor required spring data mapper
    }

    public PurchasedGymPassDocument(
            GymPassDocument gymPassDocument,
            UserDocument userDocument,
            LocalDate startDate,
            LocalDate endDate,
            int entries,
            LocalDate suspensionDate
    ){
        this.gymPassDocument = gymPassDocument;
        this.userDocument = userDocument;
        this.startDate = startDate;
        this.endDate = endDate;
        this.entries = entries;
        this.suspensionDate = suspensionDate;
    }

    public PurchasedGymPassDocument(
            GymPassDocument gymPassDocument,
            UserDocument userDocument,
            LocalDate startDate,
            LocalDate endDate,
            int entries
    ){
        this.gymPassDocument = gymPassDocument;
        this.userDocument = userDocument;
        this.startDate = startDate;
        this.endDate = endDate;
        this.entries = entries;
    }

    public String getId() {
        return id;
    }

    public GymPassDocument getGymPassDocument() {
        return gymPassDocument;
    }

    public UserDocument getUserDocument() {
        return userDocument;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setGymPassDocument(GymPassDocument gymPassDocument) {
        this.gymPassDocument = gymPassDocument;
    }

    public void setUserDocument(UserDocument userDocument) {
        this.userDocument = userDocument;
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
        return "PurchasedGymPassDocument{" +
                "id='" + id + '\'' +
                ", gymPassDocument=" + gymPassDocument +
                ", userDocument=" + userDocument +
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
        PurchasedGymPassDocument that = (PurchasedGymPassDocument) o;
        return entries == that.entries &&
                Objects.equals(id, that.id) &&
                Objects.equals(gymPassDocument, that.gymPassDocument) &&
                Objects.equals(userDocument, that.userDocument) &&
                Objects.equals(purchaseDateAndTime, that.purchaseDateAndTime) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(suspensionDate, that.suspensionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                gymPassDocument,
                userDocument,
                purchaseDateAndTime,
                startDate,
                endDate,
                entries,
                suspensionDate
        );
    }
}

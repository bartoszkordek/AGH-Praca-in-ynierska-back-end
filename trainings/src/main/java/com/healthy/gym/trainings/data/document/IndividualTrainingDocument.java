package com.healthy.gym.trainings.data.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Document(collection = "individualTraining")
public class IndividualTrainingDocument {

    @Id
    private String id;
    private String individualTrainingId;
    @DBRef
    private TrainingTypeDocument training;
    @DBRef
    private List<UserDocument> basicList;
    @DBRef
    private List<UserDocument> trainers;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    @DBRef
    private LocationDocument location;
    private String remarks;
    private boolean accepted;
    private boolean rejected;
    private boolean cancelled;

    public IndividualTrainingDocument() {
        // empty constructor required by spring data mapper
    }

    public IndividualTrainingDocument(
            String individualTrainingId,
            TrainingTypeDocument training,
            List<UserDocument> basicList,
            List<UserDocument> trainers,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            LocationDocument location,
            String remarks
    ) {
        this.individualTrainingId = individualTrainingId;
        this.training = training;
        this.basicList = basicList;
        this.trainers = trainers;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.location = location;
        this.remarks = remarks;
    }

    public TrainingTypeDocument getTraining() {
        return training;
    }

    public void setTraining(TrainingTypeDocument training) {
        this.training = training;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIndividualTrainingId() {
        return individualTrainingId;
    }

    public void setIndividualTrainingId(String individualTrainingId) {
        this.individualTrainingId = individualTrainingId;
    }

    public List<UserDocument> getBasicList() {
        return basicList;
    }

    public void setBasicList(List<UserDocument> basicList) {
        this.basicList = basicList;
    }

    public List<UserDocument> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<UserDocument> trainers) {
        this.trainers = trainers;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public LocationDocument getLocation() {
        return location;
    }

    public void setLocation(LocationDocument location) {
        this.location = location;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndividualTrainingDocument that = (IndividualTrainingDocument) o;
        return accepted == that.accepted
                && rejected == that.rejected
                && cancelled == that.cancelled
                && Objects.equals(id, that.id)
                && Objects.equals(individualTrainingId, that.individualTrainingId)
                && Objects.equals(basicList, that.basicList)
                && Objects.equals(trainers, that.trainers)
                && Objects.equals(startDateTime, that.startDateTime)
                && Objects.equals(endDateTime, that.endDateTime)
                && Objects.equals(location, that.location)
                && Objects.equals(remarks, that.remarks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                individualTrainingId,
                basicList,
                trainers,
                startDateTime,
                endDateTime,
                location,
                remarks,
                accepted,
                rejected,
                cancelled
        );
    }

    @Override
    public String toString() {
        return "IndividualTrainingDocument{" +
                "id='" + id + '\'' +
                ", individualTrainingId='" + individualTrainingId + '\'' +
                ", basicList=" + basicList +
                ", trainers=" + trainers +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", location=" + location +
                ", remarks='" + remarks + '\'' +
                ", accepted=" + accepted +
                ", rejected=" + rejected +
                ", cancelled=" + cancelled +
                '}';
    }
}

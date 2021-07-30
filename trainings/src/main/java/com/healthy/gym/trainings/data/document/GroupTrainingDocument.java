package com.healthy.gym.trainings.data.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Document(collection = "groupTraining")
public class GroupTrainingDocument {

    @Id
    private String id;
    private String groupTrainingId;
    @DBRef
    private TrainingTypeDocument training;
    @DBRef
    private List<UserDocument> trainers;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @DBRef
    private LocationDocument location;
    private int limit;
    @DBRef
    private List<UserDocument> basicList;
    @DBRef
    private List<UserDocument> reserveList;

    public GroupTrainingDocument() {
        // empty constructor required by spring data mapper
    }

    public GroupTrainingDocument(
            String groupTrainingId,
            TrainingTypeDocument training,
            List<UserDocument> trainers,
            LocalDateTime startDate,
            LocalDateTime endDate,
            LocationDocument location,
            int limit,
            List<UserDocument> basicList,
            List<UserDocument> reserveList
    ) {
        this.groupTrainingId = groupTrainingId;
        this.training = training;
        this.trainers = trainers;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.limit = limit;
        this.basicList = basicList;
        this.reserveList = reserveList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupTrainingId() {
        return groupTrainingId;
    }

    public void setGroupTrainingId(String groupTrainingId) {
        this.groupTrainingId = groupTrainingId;
    }

    public TrainingTypeDocument getTraining() {
        return training;
    }

    public void setTraining(TrainingTypeDocument training) {
        this.training = training;
    }

    public List<UserDocument> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<UserDocument> trainers) {
        this.trainers = trainers;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocationDocument getLocation() {
        return location;
    }

    public void setLocation(LocationDocument location) {
        this.location = location;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<UserDocument> getBasicList() {
        return basicList;
    }

    public void setBasicList(List<UserDocument> basicList) {
        this.basicList = basicList;
    }

    public List<UserDocument> getReserveList() {
        return reserveList;
    }

    public void setReserveList(List<UserDocument> reserveList) {
        this.reserveList = reserveList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupTrainingDocument that = (GroupTrainingDocument) o;
        return limit == that.limit
                && Objects.equals(id, that.id)
                && Objects.equals(groupTrainingId, that.groupTrainingId)
                && Objects.equals(training, that.training)
                && Objects.equals(trainers, that.trainers)
                && Objects.equals(startDate, that.startDate)
                && Objects.equals(endDate, that.endDate)
                && Objects.equals(location, that.location)
                && Objects.equals(basicList, that.basicList)
                && Objects.equals(reserveList, that.reserveList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                groupTrainingId,
                training,
                trainers,
                startDate,
                endDate,
                location,
                limit,
                basicList,
                reserveList
        );
    }

    @Override
    public String toString() {
        return "GroupTraining{" +
                "id='" + id + '\'' +
                ", groupTrainingId='" + groupTrainingId + '\'' +
                ", training=" + training +
                ", trainers=" + trainers +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", location=" + location +
                ", limit=" + limit +
                ", basicList=" + basicList +
                ", reserveList=" + reserveList +
                '}';
    }
}

package com.healthy.gym.trainings.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericTrainingDTO {

    private String id;
    private String title;
    private String startDate;
    private String endDate;
    private boolean allDay;
    private String location;
    private List<BasicUserInfoDTO> trainers;
    private ParticipantsDTO participants;
    private String remarks;
    private boolean isGroupTraining;
    private boolean accepted;
    private boolean rejected;
    private boolean cancelled;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<BasicUserInfoDTO> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<BasicUserInfoDTO> trainers) {
        this.trainers = trainers;
    }

    public ParticipantsDTO getParticipants() {
        return participants;
    }

    public void setParticipants(ParticipantsDTO participants) {
        this.participants = participants;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isGroupTraining() {
        return isGroupTraining;
    }

    public void setIsGroupTraining(boolean groupTraining) {
        isGroupTraining = groupTraining;
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
        GenericTrainingDTO that = (GenericTrainingDTO) o;
        return allDay == that.allDay
                && isGroupTraining == that.isGroupTraining
                && accepted == that.accepted
                && rejected == that.rejected
                && cancelled == that.cancelled
                && Objects.equals(id, that.id)
                && Objects.equals(title, that.title)
                && Objects.equals(startDate, that.startDate)
                && Objects.equals(endDate, that.endDate)
                && Objects.equals(location, that.location)
                && Objects.equals(trainers, that.trainers)
                && Objects.equals(participants, that.participants)
                && Objects.equals(remarks, that.remarks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                title,
                startDate,
                endDate,
                allDay,
                location,
                trainers,
                participants,
                remarks,
                isGroupTraining,
                accepted,
                rejected,
                cancelled
        );
    }

    @Override
    public String toString() {
        return "GenericTrainingDTO{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", allDay=" + allDay +
                ", location='" + location + '\'' +
                ", trainers=" + trainers +
                ", participants=" + participants +
                ", remarks='" + remarks + '\'' +
                ", isGroupTraining=" + isGroupTraining +
                ", accepted=" + accepted +
                ", rejected=" + rejected +
                ", cancelled=" + cancelled +
                '}';
    }
}

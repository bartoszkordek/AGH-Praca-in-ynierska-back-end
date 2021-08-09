package com.healthy.gym.trainings.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndividualTrainingDTO {

    @JsonProperty("id")
    private String individualTrainingId;
    private String title;
    private String startDate;
    private String endDate;
    private boolean allDay;
    private String location;
    private List<BasicUserInfoDTO> trainers;
    private ParticipantsDTO participants;

    public IndividualTrainingDTO() {
        this.participants = new ParticipantsDTO();
    }

    public IndividualTrainingDTO(
            String individualTrainingId,
            String title,
            String startDate,
            String endDate,
            boolean allDay,
            String location,
            List<BasicUserInfoDTO> trainers
    ) {
        this.individualTrainingId = individualTrainingId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.allDay = allDay;
        this.location = location;
        this.trainers = trainers;
        this.participants = new ParticipantsDTO();
    }

    public String getIndividualTrainingId() {
        return individualTrainingId;
    }

    public void setIndividualTrainingId(String individualTrainingId) {
        this.individualTrainingId = individualTrainingId;
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

    public void setBasicList(List<BasicUserInfoDTO> basicList) {
        this.participants.setBasicList(basicList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndividualTrainingDTO that = (IndividualTrainingDTO) o;
        return allDay == that.allDay
                && Objects.equals(individualTrainingId, that.individualTrainingId)
                && Objects.equals(title, that.title)
                && Objects.equals(startDate, that.startDate)
                && Objects.equals(endDate, that.endDate)
                && Objects.equals(location, that.location)
                && Objects.equals(trainers, that.trainers)
                && Objects.equals(participants, that.participants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(individualTrainingId, title, startDate, endDate, allDay, location, trainers, participants);
    }

    @Override
    public String toString() {
        return "IndividualTrainingDTO{" +
                "individualTrainingId='" + individualTrainingId + '\'' +
                ", title='" + title + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", allDay=" + allDay +
                ", location='" + location + '\'' +
                ", trainers=" + trainers +
                ", participants=" + participants +
                '}';
    }
}

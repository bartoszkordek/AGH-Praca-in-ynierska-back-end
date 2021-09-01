package com.healthy.gym.account.dto;

import java.util.Objects;

public class TrainingDTO {

    private String trainingId;
    private String title;

    public TrainingDTO() {}

    public TrainingDTO(
            String trainingId,
            String title
    ){
        this.trainingId = trainingId;
        this.title = title;
    }

    public String getTrainingId() {
        return trainingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTrainingId(String trainingId) {
        this.trainingId = trainingId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "TrainingDTO{" +
                "trainingId='" + trainingId + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingDTO that = (TrainingDTO) o;
        return Objects.equals(trainingId, that.trainingId) &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainingId, title);
    }
}

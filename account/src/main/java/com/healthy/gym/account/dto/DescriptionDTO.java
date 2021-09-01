package com.healthy.gym.account.dto;

import java.util.List;
import java.util.Objects;

public class DescriptionDTO {

    private String synopsis;
    private String full;
    private List<TrainingDTO> trainings;

    public DescriptionDTO() {}

    public DescriptionDTO(
            String synopsis,
            String full,
            List<TrainingDTO> trainings
    ) {
        this.synopsis = synopsis;
        this.full = full;
        this.trainings = trainings;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getFull() {
        return full;
    }

    public List<TrainingDTO> getTrainings() {
        return trainings;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public void setFull(String full) {
        this.full = full;
    }

    public void setTrainings(List<TrainingDTO> trainings) {
        this.trainings = trainings;
    }

    @Override
    public String toString() {
        return "DescriptionDTO{" +
                "synopsis='" + synopsis + '\'' +
                ", full='" + full + '\'' +
                ", trainings=" + trainings +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DescriptionDTO that = (DescriptionDTO) o;
        return Objects.equals(synopsis, that.synopsis) &&
                Objects.equals(full, that.full) &&
                Objects.equals(trainings, that.trainings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                synopsis,
                full,
                trainings
        );
    }
}

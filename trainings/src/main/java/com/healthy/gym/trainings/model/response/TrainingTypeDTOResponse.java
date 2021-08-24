package com.healthy.gym.trainings.model.response;

import com.healthy.gym.trainings.dto.TrainingTypeDTO;

import java.util.Objects;

public class TrainingTypeDTOResponse extends AbstractResponse {

    private TrainingTypeDTO trainingType;

    public TrainingTypeDTOResponse(String message, TrainingTypeDTO trainingTypeDTO) {
        super(message);
        this.trainingType = trainingTypeDTO;
    }

    public TrainingTypeDTO getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingTypeDTO trainingType) {
        this.trainingType = trainingType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TrainingTypeDTOResponse that = (TrainingTypeDTOResponse) o;
        return Objects.equals(trainingType, that.trainingType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), trainingType);
    }

    @Override
    public String toString() {
        return "TrainingTypeDTOResponse{" +
                "trainingTypeDTO=" + trainingType +
                "} " + super.toString();
    }
}

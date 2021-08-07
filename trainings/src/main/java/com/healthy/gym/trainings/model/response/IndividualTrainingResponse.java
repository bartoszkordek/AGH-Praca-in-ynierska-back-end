package com.healthy.gym.trainings.model.response;

import com.healthy.gym.trainings.dto.IndividualTrainingDTO;

import java.util.Map;
import java.util.Objects;

public class IndividualTrainingResponse extends AbstractResponse {
    private IndividualTrainingDTO training;

    public IndividualTrainingResponse() {
    }

    public IndividualTrainingResponse(String message, IndividualTrainingDTO training) {
        super(message);
        this.training = training;
    }

    public IndividualTrainingResponse(String message, Map<String, String> errors, IndividualTrainingDTO training) {
        super(message, errors);
        this.training = training;
    }

    public IndividualTrainingDTO getTraining() {
        return training;
    }

    public void setTraining(IndividualTrainingDTO training) {
        this.training = training;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        IndividualTrainingResponse that = (IndividualTrainingResponse) o;
        return Objects.equals(training, that.training);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), training);
    }

    @Override
    public String toString() {
        return "IndividualTrainingResponse{" +
                "training=" + training +
                "} " + super.toString();
    }
}

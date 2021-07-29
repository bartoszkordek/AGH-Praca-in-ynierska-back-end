package com.healthy.gym.trainings.model.response;

import com.healthy.gym.trainings.shared.GroupTrainingDTO;

import java.util.Map;
import java.util.Objects;

public class CreateGroupTrainingResponse extends AbstractResponse {
    private GroupTrainingDTO training;

    public CreateGroupTrainingResponse() {
    }

    public CreateGroupTrainingResponse(String message, GroupTrainingDTO training) {
        super(message);
        this.training = training;
    }

    public CreateGroupTrainingResponse(String message, Map<String, String> errors, GroupTrainingDTO training) {
        super(message, errors);
        this.training = training;
    }

    public GroupTrainingDTO getTraining() {
        return training;
    }

    public void setTraining(GroupTrainingDTO training) {
        this.training = training;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CreateGroupTrainingResponse that = (CreateGroupTrainingResponse) o;
        return Objects.equals(training, that.training);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), training);
    }

    @Override
    public String toString() {
        return "CreateGroupTrainingResponse{" +
                "training=" + training +
                "} " + super.toString();
    }
}

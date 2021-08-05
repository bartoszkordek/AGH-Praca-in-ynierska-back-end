package com.healthy.gym.trainings.model.response;

import com.healthy.gym.trainings.dto.GroupTrainingDTO;

import java.util.Map;
import java.util.Objects;

public class GroupTrainingResponse extends AbstractResponse {
    private GroupTrainingDTO training;

    public GroupTrainingResponse() {
    }

    public GroupTrainingResponse(String message, GroupTrainingDTO training) {
        super(message);
        this.training = training;
    }

    public GroupTrainingResponse(String message, Map<String, String> errors, GroupTrainingDTO training) {
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
        GroupTrainingResponse that = (GroupTrainingResponse) o;
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

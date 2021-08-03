package com.healthy.gym.trainings.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.trainings.shared.GetGroupTrainingDTO;

import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

public class GroupTrainingsResponse {

    @NotNull
    @JsonProperty("data")
    private final List<GetGroupTrainingDTO> groupTrainings;

    public GroupTrainingsResponse(
            @JsonProperty("data") List<GetGroupTrainingDTO> groupTrainings

    ) {
        this.groupTrainings = groupTrainings;

    }

    @Override
    public String toString() {
        return "GroupTrainingResponse{" +
                "groupTrainings=" + groupTrainings +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupTrainingsResponse that = (GroupTrainingsResponse) o;
        return Objects.equals(groupTrainings, that.groupTrainings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupTrainings);
    }

    public List<GetGroupTrainingDTO> getGroupTrainings() {
        return groupTrainings;
    }
}

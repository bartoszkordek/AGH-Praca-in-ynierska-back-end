package com.healthy.gym.trainings.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.trainings.shared.GetGroupTrainingPublicDTO;

import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;


public class GroupTrainingsPublicResponse {

    @NotNull
    @JsonProperty("data")
    private final List<GetGroupTrainingPublicDTO> groupTrainings;

    public GroupTrainingsPublicResponse(
            @JsonProperty("data") List<GetGroupTrainingPublicDTO> groupTrainings

    ) {
        this.groupTrainings = groupTrainings;
    }

    public List<GetGroupTrainingPublicDTO> getGroupTrainings() {
        return groupTrainings;
    }

    @Override
    public String toString() {
        return "GroupTrainingPublicResponse{" +
                "groupTrainings=" + groupTrainings +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupTrainingsPublicResponse that = (GroupTrainingsPublicResponse) o;
        return Objects.equals(groupTrainings, that.groupTrainings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupTrainings);
    }
}

package com.healthy.gym.trainings.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.trainings.shared.BasicUserInfoDTO;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

public class GroupTrainingParticipantsResponse {

    @NotNull
    @JsonProperty("data")
    private final List<BasicUserInfoDTO> users;

    public GroupTrainingParticipantsResponse(List<BasicUserInfoDTO> users){
        this.users = users;
    }

    public List<BasicUserInfoDTO> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "GroupTrainingParticipantsResponse{" +
                "users=" + users +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupTrainingParticipantsResponse that = (GroupTrainingParticipantsResponse) o;
        return Objects.equals(users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(users);
    }
}

package com.healthy.gym.account.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrainerRequest {

    @NotNull
    private String userId;

    @Size(min = 2, max = 60, message = "{field.synopsis.failure}")
    private String synopsis;

    @Size(min = 2, max = 500, message = "{field.full.failure}")
    private String full;

    List<String> trainingIds;

    public String getUserId() {
        return userId;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getFull() {
        return full;
    }

    public List<String> getTrainingIds() {
        return trainingIds;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public void setFull(String full) {
        this.full = full;
    }

    public void setTrainingIds(List<String> trainingIds) {
        this.trainingIds = trainingIds;
    }

    @Override
    public String toString() {
        return "TrainerRequest{" +
                "userId='" + userId + '\'' +
                ", synopsis='" + synopsis + '\'' +
                ", full='" + full + '\'' +
                ", trainingIds=" + trainingIds +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainerRequest that = (TrainerRequest) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(synopsis, that.synopsis) &&
                Objects.equals(full, that.full) &&
                Objects.equals(trainingIds, that.trainingIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                userId,
                synopsis,
                full,
                trainingIds
        );
    }
}

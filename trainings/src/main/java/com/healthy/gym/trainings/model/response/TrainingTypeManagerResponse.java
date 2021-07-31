package com.healthy.gym.trainings.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Objects;

public class TrainingTypeManagerResponse extends TrainingTypePublicResponse {

    @JsonProperty("_id")
    private String id;

    private String trainingName;

    private String description;

    private byte[] avatar;

    public TrainingTypeManagerResponse(
            String id,
            String trainingName,
            String description,
            byte[] avatar
    ) {
        super(trainingName, description, avatar);
        this.id = id;
        this.trainingName = trainingName;
        this.description = description;
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getTrainingName() {
        return trainingName;
    }

    @Override
    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public byte[] getAvatar() {
        return avatar;
    }

    @Override
    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TrainingTypeManagerResponse that = (TrainingTypeManagerResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(trainingName, that.trainingName) && Objects.equals(description, that.description) && Arrays.equals(avatar, that.avatar);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), id, trainingName, description);
        result = 31 * result + Arrays.hashCode(avatar);
        return result;
    }

    @Override
    public String toString() {
        return "TrainingTypeManagerResponse{" +
                "id='" + id + '\'' +
                ", trainingName='" + trainingName + '\'' +
                ", description='" + description + '\'' +
                ", avatar=" + Arrays.toString(avatar) +
                "} " + super.toString();
    }
}

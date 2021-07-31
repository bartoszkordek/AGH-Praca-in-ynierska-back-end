package com.healthy.gym.trainings.model.response;

import java.util.Arrays;
import java.util.Objects;

public class TrainingTypePublicResponse {

    private String trainingName;
    private String description;
    private byte[] avatar;

    public TrainingTypePublicResponse(
            String trainingName,
            String description,
            byte[] avatar
    ) {
        this.trainingName = trainingName;
        this.description = description;
        this.avatar = avatar;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingTypePublicResponse that = (TrainingTypePublicResponse) o;
        return Objects.equals(trainingName, that.trainingName)
                && Objects.equals(description, that.description)
                && Arrays.equals(avatar, that.avatar);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(trainingName, description);
        result = 31 * result + Arrays.hashCode(avatar);
        return result;
    }

    @Override
    public String toString() {
        return "TrainingTypePublicResponse{" +
                "trainingName='" + trainingName + '\'' +
                ", description='" + description + '\'' +
                ", avatar=" + Arrays.toString(avatar) +
                '}';
    }
}

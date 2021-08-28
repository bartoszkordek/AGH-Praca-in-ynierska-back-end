package com.healthy.gym.equipment.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EquipmentRequest {

    @NotNull(message = "{field.required}")
    @Size(min = 2, max = 1000, message = "{field.required}")
    private String title;

    @NotNull(message = "{field.required}")
    @Size(min = 2, max = 1000, message = "{field.required}")
    private String synopsis;

    @NotNull(message = "{field.required}")
    List<String> trainingIds;

    public String getTitle() {
        return title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public List<String> getTrainingIds() {
        return trainingIds;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public void setTrainingIds(List<String> trainingIds) {
        this.trainingIds = trainingIds;
    }

    @Override
    public String toString() {
        return "EquipmentRequest{" +
                "title='" + title + '\'' +
                ", synopsis='" + synopsis + '\'' +
                ", trainingIds=" + trainingIds +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentRequest that = (EquipmentRequest) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(synopsis, that.synopsis) &&
                Objects.equals(trainingIds, that.trainingIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                title,
                synopsis,
                trainingIds
        );
    }
}

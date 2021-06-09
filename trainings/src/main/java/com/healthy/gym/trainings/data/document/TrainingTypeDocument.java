package com.healthy.gym.trainings.data.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.util.Objects;

@Document(collection = "trainingTypes")
public class TrainingTypeDocument {

    @Id
    private String id;
    private String trainingTypeId;
    private String name;
    private String description;
    private Duration duration;
    @DBRef
    private ImageDocument imageDocument;

    public TrainingTypeDocument() {
        // empty constructor required by spring data mapper
    }

    public TrainingTypeDocument(
            String trainingTypeId,
            String name,
            String description,
            Duration duration,
            ImageDocument imageDocument
    ) {
        this.trainingTypeId = trainingTypeId;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.imageDocument = imageDocument;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrainingTypeId() {
        return trainingTypeId;
    }

    public void setTrainingTypeId(String trainingTypeId) {
        this.trainingTypeId = trainingTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public ImageDocument getImageDocument() {
        return imageDocument;
    }

    public void setImageDocument(ImageDocument imageDocument) {
        this.imageDocument = imageDocument;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingTypeDocument that = (TrainingTypeDocument) o;
        return Objects.equals(id, that.id)
                && Objects.equals(trainingTypeId, that.trainingTypeId)
                && Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(duration, that.duration)
                && Objects.equals(imageDocument, that.imageDocument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, trainingTypeId, name, description, duration, imageDocument);
    }

    @Override
    public String toString() {
        return "TrainingTypeDocument{" +
                "id='" + id + '\'' +
                ", trainingTypeId='" + trainingTypeId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", imageDocument=" + imageDocument +
                '}';
    }
}

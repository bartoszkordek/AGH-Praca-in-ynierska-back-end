package com.healthy.gym.equipment.data.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

@Document(collection = "equipments")
public class EquipmentDocument {

    @Id
    private String id;
    private String equipmentId;
    private String title;
    private List<String> images;
    private String synopsis;
    @DBRef
    private List<TrainingTypeDocument> trainings;

    public EquipmentDocument() {
    }

    public EquipmentDocument(
            String equipmentId,
            String title,
            List<String> images,
            String synopsis,
            List<TrainingTypeDocument> trainings
    ) {
        this.equipmentId = equipmentId;
        this.title = title;
        this.images = images;
        this.synopsis = synopsis;
        this.trainings = trainings;
    }

    public String getId() {
        return id;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getImages() {
        return images;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public List<TrainingTypeDocument> getTrainings() {
        return trainings;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public void setTrainings(List<TrainingTypeDocument> trainings) {
        this.trainings = trainings;
    }

    @Override
    public String toString() {
        return "EquipmentDocument{" +
                "id='" + id + '\'' +
                ", equipmentId='" + equipmentId + '\'' +
                ", title='" + title + '\'' +
                ", images=" + images +
                ", synopsis='" + synopsis + '\'' +
                ", trainings=" + trainings +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentDocument that = (EquipmentDocument) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(equipmentId, that.equipmentId) &&
                Objects.equals(title, that.title) &&
                Objects.equals(images, that.images) &&
                Objects.equals(synopsis, that.synopsis) &&
                Objects.equals(trainings, that.trainings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                equipmentId,
                title,
                images,
                synopsis,
                trainings
        );
    }
}




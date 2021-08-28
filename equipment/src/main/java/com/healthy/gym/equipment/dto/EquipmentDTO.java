package com.healthy.gym.equipment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EquipmentDTO {

    private String equipmentId;
    private String title;
    private List<String> images;
    private DescriptionDTO description;

    public EquipmentDTO() {}

    public EquipmentDTO(
            String equipmentId,
            String title,
            List<String> images,
            DescriptionDTO description
    ) {
        this.equipmentId = equipmentId;
        this.title = title;
        this.images = images;
        this.description = description;
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

    public DescriptionDTO getDescription() {
        return description;
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

    public void setDescription(DescriptionDTO description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "EquipmentDTO{" +
                "equipmentId='" + equipmentId + '\'' +
                ", title='" + title + '\'' +
                ", images=" + images +
                ", description=" + description +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentDTO that = (EquipmentDTO) o;
        return Objects.equals(equipmentId, that.equipmentId) &&
                Objects.equals(title, that.title) &&
                Objects.equals(images, that.images) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                equipmentId,
                title,
                images,
                description
        );
    }
}

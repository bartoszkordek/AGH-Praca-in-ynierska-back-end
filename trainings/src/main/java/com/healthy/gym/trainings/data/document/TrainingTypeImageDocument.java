package com.healthy.gym.trainings.data.document;

import org.apache.http.entity.ContentType;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "trainingTypeImage")
public class TrainingTypeImageDocument {

    @Id
    private String id;
    private String imageId;
    private Binary imageData;
    private ContentType contentType;

    public TrainingTypeImageDocument() {
        //empty constructor required by spring data mapper
    }

    public TrainingTypeImageDocument(String imageId, Binary imageData, ContentType contentType) {
        this.imageId = imageId;
        this.imageData = imageData;
        this.contentType = contentType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public Binary getImageData() {
        return imageData;
    }

    public void setImageData(Binary imageData) {
        this.imageData = imageData;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingTypeImageDocument that = (TrainingTypeImageDocument) o;
        return Objects.equals(id, that.id)
                && Objects.equals(imageId, that.imageId)
                && Objects.equals(imageData, that.imageData)
                && Objects.equals(contentType, that.contentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, imageId, imageData, contentType);
    }

    @Override
    public String toString() {
        return "TrainingTypeImageDocument{" +
                "imageId='" + imageId + '\'' +
                ", imageData=" + imageData +
                ", contentType=" + contentType +
                '}';
    }
}

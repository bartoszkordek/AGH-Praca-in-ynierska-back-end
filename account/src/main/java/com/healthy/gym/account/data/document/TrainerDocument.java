package com.healthy.gym.account.data.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

@Document(collection = "trainers")
public class TrainerDocument {

    @Id
    private String id;
    @DBRef
    private UserDocument userDocument;
    private List<ImageDocument> imagesDocuments;
    private List<String> images;
    private String synopsis;
    private String full;
    @DBRef
    private List<TrainingTypeDocument> trainingTypeDocuments;

    public TrainerDocument() {}

    public TrainerDocument(
            UserDocument userDocument,
            List<ImageDocument> imagesDocuments,
            List<String> images,
            String synopsis,
            String full,
            List<TrainingTypeDocument> trainingTypeDocuments
    ) {
        this.userDocument = userDocument;
        this.imagesDocuments = imagesDocuments;
        this.images = images;
        this.synopsis = synopsis;
        this.full = full;
        this.trainingTypeDocuments = trainingTypeDocuments;
    }

    public String getId() {
        return id;
    }

    public UserDocument getUserDocument() {
        return userDocument;
    }

    public List<ImageDocument> getImagesDocuments() {
        return imagesDocuments;
    }

    public List<String> getImages() {
        return images;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getFull() {
        return full;
    }

    public List<TrainingTypeDocument> getTrainingTypeDocuments() {
        return trainingTypeDocuments;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserDocument(UserDocument userDocument) {
        this.userDocument = userDocument;
    }

    public void setImagesDocuments(List<ImageDocument> imagesDocuments) {
        this.imagesDocuments = imagesDocuments;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public void setFull(String full) {
        this.full = full;
    }

    public void setTrainingTypeDocuments(List<TrainingTypeDocument> trainingTypeDocuments) {
        this.trainingTypeDocuments = trainingTypeDocuments;
    }

    @Override
    public String toString() {
        return "TrainerDocument{" +
                "id='" + id + '\'' +
                ", userDocument=" + userDocument +
                ", imagesDocuments=" + imagesDocuments +
                ", images=" + images +
                ", synopsis='" + synopsis + '\'' +
                ", full='" + full + '\'' +
                ", trainingTypeDocuments=" + trainingTypeDocuments +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainerDocument that = (TrainerDocument) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userDocument, that.userDocument) &&
                Objects.equals(imagesDocuments, that.imagesDocuments) &&
                Objects.equals(images, that.images) &&
                Objects.equals(synopsis, that.synopsis) &&
                Objects.equals(full, that.full) &&
                Objects.equals(trainingTypeDocuments, that.trainingTypeDocuments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                userDocument,
                imagesDocuments,
                images,
                synopsis,
                full,
                trainingTypeDocuments
        );
    }
}

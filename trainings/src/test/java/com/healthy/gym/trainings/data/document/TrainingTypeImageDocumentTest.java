package com.healthy.gym.trainings.data.document;

import org.apache.http.entity.ContentType;
import org.bson.types.Binary;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingTypeImageDocumentTest {

    @Test
    void twoObjectOfEmptyTrainingTypeImageDocumentShouldBeEqual() {
        TrainingTypeImageDocument imageDocument1 = new TrainingTypeImageDocument();
        TrainingTypeImageDocument imageDocument2 = new TrainingTypeImageDocument();

        assertThat(imageDocument1)
                .isEqualTo(imageDocument2)
                .hasSameHashCodeAs(imageDocument2);
    }

    @Test
    void twoDifferentObjectsOfTrainingTypeImageDocumentShouldNotBeEqual() {
        String imageTypeId = UUID.randomUUID().toString();
        Binary imageDate = new Binary("sample data".getBytes(StandardCharsets.UTF_8));

        TrainingTypeImageDocument imageDocument1 =
                new TrainingTypeImageDocument(imageTypeId, imageDate, ContentType.IMAGE_PNG);
        TrainingTypeImageDocument imageDocument2 =
                new TrainingTypeImageDocument(imageTypeId, imageDate, ContentType.IMAGE_JPEG);

        assertThat(imageDocument1).isNotEqualTo(imageDocument2);
        assertThat(imageDocument1.hashCode()).isNotEqualTo(imageDocument2.hashCode());
    }

    @Test
    void twoObjectsOfTrainingTypeImageDocumentWithSameFieldValuesShouldBeEqual() {
        String imageTypeId = UUID.randomUUID().toString();
        ContentType contentType = ContentType.IMAGE_PNG;
        Binary imageDate = new Binary("sample data".getBytes(StandardCharsets.UTF_8));

        TrainingTypeImageDocument imageDocument1 = new TrainingTypeImageDocument(imageTypeId, imageDate, contentType);
        TrainingTypeImageDocument imageDocument2 = new TrainingTypeImageDocument(imageTypeId, imageDate, contentType);

        assertThat(imageDocument1)
                .isEqualTo(imageDocument2)
                .hasSameHashCodeAs(imageDocument2);
    }

    @Test
    void shouldAllFieldsBeNullWhenCreated() {
        assertThat(new TrainingTypeImageDocument()).hasAllNullFieldsOrProperties();
    }
}
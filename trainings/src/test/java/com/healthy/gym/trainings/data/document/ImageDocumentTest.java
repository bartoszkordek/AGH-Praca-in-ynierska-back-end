package com.healthy.gym.trainings.data.document;

import org.apache.http.entity.ContentType;
import org.bson.types.Binary;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ImageDocumentTest {

    @Test
    void twoObjectOfEmptyTrainingTypeImageDocumentShouldBeEqual() {
        ImageDocument imageDocument1 = new ImageDocument();
        ImageDocument imageDocument2 = new ImageDocument();

        assertThat(imageDocument1)
                .isEqualTo(imageDocument2)
                .hasSameHashCodeAs(imageDocument2);
    }

    @Test
    void twoDifferentObjectsOfTrainingTypeImageDocumentShouldNotBeEqual() {
        String imageTypeId = UUID.randomUUID().toString();
        Binary imageDate = new Binary("sample data".getBytes(StandardCharsets.UTF_8));

        ImageDocument imageDocument1 =
                new ImageDocument(imageTypeId, imageDate, ContentType.IMAGE_PNG.getMimeType());
        ImageDocument imageDocument2 =
                new ImageDocument(imageTypeId, imageDate, ContentType.IMAGE_JPEG.getMimeType());

        assertThat(imageDocument1).isNotEqualTo(imageDocument2);
        assertThat(imageDocument1.hashCode()).isNotEqualTo(imageDocument2.hashCode());
    }

    @Test
    void twoObjectsOfTrainingTypeImageDocumentWithSameFieldValuesShouldBeEqual() {
        String imageTypeId = UUID.randomUUID().toString();
        String contentType = ContentType.IMAGE_PNG.getMimeType();
        Binary imageDate = new Binary("sample data".getBytes(StandardCharsets.UTF_8));

        ImageDocument imageDocument1 = new ImageDocument(imageTypeId, imageDate, contentType);
        ImageDocument imageDocument2 = new ImageDocument(imageTypeId, imageDate, contentType);

        assertThat(imageDocument1)
                .isEqualTo(imageDocument2)
                .hasSameHashCodeAs(imageDocument2);
    }

    @Test
    void shouldAllFieldsBeNullWhenCreated() {
        assertThat(new ImageDocument()).hasAllNullFieldsOrProperties();
    }
}
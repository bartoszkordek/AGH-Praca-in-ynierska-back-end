package com.healthy.gym.trainings.data.document;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingTypeDocumentTest {

    @Test
    void twoObjectOfEmptyTrainingTypeDocumentShouldBeEqual() {
        TrainingTypeDocument trainingTypeDocument1 = new TrainingTypeDocument();
        TrainingTypeDocument trainingTypeDocument2 = new TrainingTypeDocument();

        assertThat(trainingTypeDocument1)
                .isEqualTo(trainingTypeDocument2)
                .hasSameHashCodeAs(trainingTypeDocument2);
    }

    @Test
    void twoDifferentObjectsOfTrainingTypeDocumentShouldNotBeEqual() {
        TrainingTypeDocument trainingTypeDocument1 = new TrainingTypeDocument(
                UUID.randomUUID().toString(),
                "Test name",
                "Test description",
                null,
                null
        );
        TrainingTypeDocument trainingTypeDocument2 = new TrainingTypeDocument(
                UUID.randomUUID().toString(),
                "Test name1",
                "Test description2",
                null,
                null
        );

        assertThat(trainingTypeDocument1).isNotEqualTo(trainingTypeDocument2);
        assertThat(trainingTypeDocument1.hashCode())
                .isNotEqualTo(trainingTypeDocument2.hashCode());
    }

    @Test
    void twoDifferentObjectsOfTrainingTypeDocumentWithSameFieldValuesShouldBeEqual() {
        String id = UUID.randomUUID().toString();
        TrainingTypeDocument trainingTypeDocument1 = new TrainingTypeDocument(
                id,
                "Test name",
                "Test description",
                null,
                null
        );
        TrainingTypeDocument trainingTypeDocument2 = new TrainingTypeDocument(
                id,
                "Test name",
                "Test description",
                null,
                null
        );

        assertThat(trainingTypeDocument1)
                .isEqualTo(trainingTypeDocument2)
                .hasSameHashCodeAs(trainingTypeDocument2);
    }

    @Test
    void shouldAllFieldsBeNullWhenCreated() {
        assertThat(new TrainingTypeDocument()).hasAllNullFieldsOrProperties();
    }
}
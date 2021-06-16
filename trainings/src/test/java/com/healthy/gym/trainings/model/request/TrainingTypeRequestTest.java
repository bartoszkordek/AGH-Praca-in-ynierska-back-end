package com.healthy.gym.trainings.model.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingTypeRequestTest {

    @Test
    void twoObjectOfEmptyTrainingTypeRequestShouldBeEqual() {
        TrainingTypeRequest trainingTypeRequest = new TrainingTypeRequest();
        TrainingTypeRequest trainingTypeRequest1 = new TrainingTypeRequest();

        assertThat(trainingTypeRequest)
                .isEqualTo(trainingTypeRequest1)
                .hasSameHashCodeAs(trainingTypeRequest1);
    }

    @Test
    void twoDifferentObjectsOfTrainingTypeRequestShouldNotBeEqual() {

        TrainingTypeRequest trainingTypeRequest = new TrainingTypeRequest();
        trainingTypeRequest.setName("Test name");
        TrainingTypeRequest trainingTypeRequest1 = new TrainingTypeRequest();

        assertThat(trainingTypeRequest).isNotEqualTo(trainingTypeRequest1);
        assertThat(trainingTypeRequest.hashCode()).isNotEqualTo(trainingTypeRequest1.hashCode());
    }

    @Test
    void twoObjectsOfTrainingTypeRequestWithSameFieldValuesShouldBeEqual() {
        TrainingTypeRequest trainingTypeRequest = new TrainingTypeRequest();
        trainingTypeRequest.setName("Test name");
        trainingTypeRequest.setDescription("Test description");

        TrainingTypeRequest trainingTypeRequest1 = new TrainingTypeRequest();
        trainingTypeRequest1.setName("Test name");
        trainingTypeRequest1.setDescription("Test description");

        assertThat(trainingTypeRequest1)
                .isEqualTo(trainingTypeRequest)
                .hasSameHashCodeAs(trainingTypeRequest);
    }

    @Test
    void shouldAllFieldsBeNullWhenCreated() {
        assertThat(new TrainingTypeRequest()).hasAllNullFieldsOrProperties();
    }

}
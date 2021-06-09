package com.healthy.gym.trainings.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class TrainingTypeServiceTest {

    @Autowired
    private TrainingTypeService trainingTypeService;

    @Test
    void name() {
        //initial test
        assertThat(true).isTrue();
    }
}
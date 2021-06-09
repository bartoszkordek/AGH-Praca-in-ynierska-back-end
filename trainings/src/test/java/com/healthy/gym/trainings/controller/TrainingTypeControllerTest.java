package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.service.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(TrainingTypeController.class)
class TrainingTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingTypeService trainingTypeService;

    @Test
    void name() {
        assertThat(true).isTrue();
    }
}
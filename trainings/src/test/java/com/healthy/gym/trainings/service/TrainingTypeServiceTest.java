package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class TrainingTypeServiceTest {

    @Autowired
    private TrainingTypeService trainingTypeService;

    @MockBean
    private TrainingTypeDAO trainingTypeRepository;

    @Test
    void name() {
    }
}
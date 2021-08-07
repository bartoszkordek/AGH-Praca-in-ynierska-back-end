package com.healthy.gym.trainings.controller.individual.training;

import com.healthy.gym.trainings.data.document.IndividualTrainings;
import com.healthy.gym.trainings.exception.RestException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.service.individual.training.IndividualTrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/individual")
public class IndividualTrainingController {

    private final IndividualTrainingService individualTrainingsService;

    @Autowired
    public IndividualTrainingController(IndividualTrainingService individualTrainingsService) {
        this.individualTrainingsService = individualTrainingsService;
    }

    @GetMapping("/{trainingId}")
    public IndividualTrainings getIndividualTrainingById(
            @PathVariable("trainingId") final String trainingId
    ) throws RestException {
        try {
            return individualTrainingsService.getIndividualTrainingById(trainingId);
        } catch (NotExistingIndividualTrainingException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    //TODO only for admin, paginacja, filtrowanie po datach
    @GetMapping
    public List<IndividualTrainings> getAllIndividualTrainingRequests() {
        return individualTrainingsService.getAllIndividualTrainings();
    }

    //TODO only for admin, paginacja, filtrowanie po datach
    @GetMapping("/all/accepted")
    public List<IndividualTrainings> getAllAcceptedIndividualTrainingRequests() {
        return individualTrainingsService.getAllAcceptedIndividualTrainings();
    }


}

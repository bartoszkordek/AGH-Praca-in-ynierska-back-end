package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.entity.TrainingType;
import com.healthy.gym.trainings.exception.DuplicatedTrainingTypes;
import com.healthy.gym.trainings.exception.NotExistingTrainingType;
import com.healthy.gym.trainings.exception.RestException;
import com.healthy.gym.trainings.model.TrainingTypeModel;
import com.healthy.gym.trainings.model.TrainingTypePublicViewModel;
import com.healthy.gym.trainings.service.TrainingTypeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/trainingType")
public class TrainingTypeController {
    //TODO Grzegorz

    private static final String GENERIC_ERROR_MESSAGE_WHILE_ISSUES_WITH_PHOTO_PROCESSING =
            "Errors while proto processing.";
    private final TrainingTypeServiceImpl trainingTypeService;

    @Autowired
    public TrainingTypeController(TrainingTypeServiceImpl trainingTypeService) {
        this.trainingTypeService = trainingTypeService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TrainingType createTrainingType(
            @RequestParam("trainingName") String trainingName,
            @RequestParam("description") String description,
            @RequestParam("avatar") MultipartFile multipartFile
    ) throws RestException {
        try {
            TrainingTypeModel trainingTypeModel = new TrainingTypeModel(trainingName, description);
            return trainingTypeService.createTrainingType(trainingTypeModel, multipartFile.getBytes());
        } catch (DuplicatedTrainingTypes e) {
            throw new RestException(e.getMessage(), HttpStatus.CONFLICT, e);
        } catch (IOException exception) {
            throw new RestException(GENERIC_ERROR_MESSAGE_WHILE_ISSUES_WITH_PHOTO_PROCESSING, HttpStatus.BAD_REQUEST, exception);
        }
    }

    @GetMapping("/{trainingTypeId}")
    public TrainingType getTrainingTypeById(
            @PathVariable("trainingTypeId") final String trainingTypeId
    ) throws RestException {
        try {
            return trainingTypeService.getTrainingTypeById(trainingTypeId);
        } catch (NotExistingTrainingType e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @GetMapping
    public List<? extends TrainingTypePublicViewModel> getAllTrainingTypes(
            @RequestParam("publicView") boolean publicView
    ) {
        if (!publicView) {
            return trainingTypeService.getAllTrainingTypesManagerView();
        } else {
            return trainingTypeService.getAllTrainingTypesPublicView();
        }
    }

    @PutMapping(
            value = "/{trainingTypeId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TrainingType updateTrainingTypeById(
            @PathVariable("trainingTypeId") final String trainingTypeId,
            @RequestParam("trainingName") String trainingName,
            @RequestParam("description") String description,
            @RequestParam("avatar") MultipartFile multipartFile
    ) throws RestException {
        try {
            TrainingTypeModel trainingTypeModel = new TrainingTypeModel(trainingName, description);
            return trainingTypeService.updateTrainingTypeById(trainingTypeId, trainingTypeModel, multipartFile.getBytes());
        } catch (NotExistingTrainingType | IOException | DuplicatedTrainingTypes e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    // TODO zmienić z trainingName na trainingID
    @DeleteMapping("/{trainingTypeId}")
    public TrainingType removeTrainingTypeByName(
            @PathVariable("trainingName") final String trainingName
    ) throws RestException {
        try {
            return trainingTypeService.removeTrainingTypeByName(trainingName);
        } catch (NotExistingTrainingType e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

}

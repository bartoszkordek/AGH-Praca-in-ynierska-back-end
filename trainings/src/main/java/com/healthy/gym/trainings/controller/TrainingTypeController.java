package com.healthy.gym.trainings.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.trainings.component.ImageValidator;
import com.healthy.gym.trainings.component.MultipartFileValidator;
import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.exception.DuplicatedTrainingTypeException;
import com.healthy.gym.trainings.exception.MultipartBodyException;
import com.healthy.gym.trainings.exception.NotExistingTrainingType;
import com.healthy.gym.trainings.exception.RestException;
import com.healthy.gym.trainings.model.other.TrainingTypeModel;
import com.healthy.gym.trainings.model.request.TrainingTypeRequest;
import com.healthy.gym.trainings.model.response.TrainingTypePublicResponse;
import com.healthy.gym.trainings.model.response.TrainingTypeResponse;
import com.healthy.gym.trainings.service.TrainingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.activation.UnsupportedDataTypeException;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/trainingType")
public class TrainingTypeController {
    //TODO Grzegorz

    private static final String GENERIC_ERROR_MESSAGE_WHILE_ISSUES_WITH_PHOTO_PROCESSING =
            "Errors while proto processing.";
    private final TrainingTypeService trainingTypeService;
    private final ObjectMapper objectMapper;
    private final Translator translator;
    private final MultipartFileValidator multipartFileValidator;
    private final ImageValidator imageValidator;

    @Autowired
    public TrainingTypeController(
            TrainingTypeService trainingTypeService,
            Translator translator,
            MultipartFileValidator multipartFileValidator,
            ImageValidator imageValidator
    ) {
        this.trainingTypeService = trainingTypeService;
        this.translator = translator;
        this.multipartFileValidator = multipartFileValidator;
        this.imageValidator = imageValidator;
        this.objectMapper = new ObjectMapper();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TrainingTypeResponse> createTrainingType(
            @RequestPart(value = "body") TrainingTypeRequest trainingTypeRequest,
            @RequestPart(value = "image", required = false) MultipartFile multipartFile
    ) {
        TrainingTypeResponse response = new TrainingTypeResponse();
        try {
            multipartFileValidator.validateBody(trainingTypeRequest);
            imageValidator.isFileSupported(multipartFile);

            trainingTypeService.createTrainingType(trainingTypeRequest, multipartFile);

            String message = translator.toLocale("training.type.created");
            response.setMessage(message);
            response.setName(trainingTypeRequest.getName());
            response.setDescription(trainingTypeRequest.getDescription());
            String imageBas64 = Base64.getEncoder().encodeToString(multipartFile.getBytes());
            response.setImageBase64Encoded(imageBas64);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);

        } catch (MultipartBodyException exception) {
            String message = translator.toLocale("exception.multipart.body");
            response.setMessage(message);
            response.setErrors(exception.getErrorMap());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);

        } catch (UnsupportedDataTypeException exception) {
            String reason = translator.toLocale("exception.unsupported.data.type");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (DuplicatedTrainingTypeException exception) {
            String reason = translator.toLocale("exception.duplicated.training.type");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @GetMapping("/{trainingTypeId}")
    public TrainingTypeDocument getTrainingTypeById(
            @PathVariable("trainingTypeId") final String trainingTypeId
    ) throws RestException {
        try {
            return trainingTypeService.getTrainingTypeById(trainingTypeId);
        } catch (NotExistingTrainingType e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @GetMapping
    public List<? extends TrainingTypePublicResponse> getAllTrainingTypes(
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
    public TrainingTypeDocument updateTrainingTypeById(
            @PathVariable("trainingTypeId") final String trainingTypeId,
            @RequestParam("trainingName") String trainingName,
            @RequestParam("description") String description,
            @RequestParam("avatar") MultipartFile multipartFile
    ) throws RestException {
        try {
            TrainingTypeModel trainingTypeModel = new TrainingTypeModel(trainingName, description);
            return trainingTypeService.updateTrainingTypeById(trainingTypeId, trainingTypeModel, multipartFile.getBytes());
        } catch (NotExistingTrainingType | IOException | DuplicatedTrainingTypeException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    // TODO zmienić z trainingName na trainingID
    @DeleteMapping("/{trainingTypeId}")
    public TrainingTypeDocument removeTrainingTypeByName(
            @PathVariable("trainingName") final String trainingName
    ) throws RestException {
        try {
            return trainingTypeService.removeTrainingTypeByName(trainingName);
        } catch (NotExistingTrainingType e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

}

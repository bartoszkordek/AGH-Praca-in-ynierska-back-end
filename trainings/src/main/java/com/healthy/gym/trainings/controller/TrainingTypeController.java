package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.component.ImageValidator;
import com.healthy.gym.trainings.component.MultipartFileValidator;
import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.data.document.ImageDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.exception.DuplicatedTrainingTypeException;
import com.healthy.gym.trainings.exception.MultipartBodyException;
import com.healthy.gym.trainings.exception.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.request.TrainingTypeRequest;
import com.healthy.gym.trainings.model.response.TrainingTypeResponse;
import com.healthy.gym.trainings.service.TrainingTypeService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.activation.UnsupportedDataTypeException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/trainingType")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;
    private final Translator translator;
    private final MultipartFileValidator multipartFileValidator;
    private final ImageValidator imageValidator;
    private final ModelMapper modelMapper;

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
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
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

            TrainingTypeDocument trainingTypeDocument =
                    trainingTypeService.createTrainingType(trainingTypeRequest, multipartFile);

            response = modelMapper.map(trainingTypeDocument, TrainingTypeResponse.class);

            String message = translator.toLocale("training.type.created");
            response.setMessage(message);
            String imageBase64 = getUpdatedImageAsBase64String(trainingTypeDocument);
            response.setImageBase64Encoded(imageBase64);

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

    private String getUpdatedImageAsBase64String(TrainingTypeDocument trainingTypeDocument) {
        ImageDocument imageDocument = trainingTypeDocument.getImageDocument();
        if (imageDocument == null) return null;
        byte[] updatedMultipartFile = imageDocument.getImageData().getData();
        return Base64.getEncoder().encodeToString(updatedMultipartFile);
    }

    @GetMapping("/{trainingTypeId}")
    public ResponseEntity<TrainingTypeResponse> getTrainingTypeById(@PathVariable final String trainingTypeId) {
        try {
            TrainingTypeDocument trainingTypeDocument = trainingTypeService.getTrainingTypeById(trainingTypeId);

            TrainingTypeResponse trainingTypeResponse = modelMapper
                    .map(trainingTypeDocument, TrainingTypeResponse.class);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(trainingTypeResponse);

        } catch (TrainingTypeNotFoundException exception) {
            String reason = translator.toLocale("exception.not.found.training.type");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @GetMapping
    public ResponseEntity<List<TrainingTypeResponse>> getAllTrainingTypes() {
        try {
            List<TrainingTypeDocument> trainingTypes = trainingTypeService.getAllTrainingTypes();
            List<TrainingTypeResponse> trainingTypeResponseList = mapTrainingDocumentToTrainingResponse(trainingTypes);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(trainingTypeResponseList);

        } catch (TrainingTypeNotFoundException exception) {
            String reason = translator.toLocale("exception.not.found.training.type.all");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    private List<TrainingTypeResponse> mapTrainingDocumentToTrainingResponse(List<TrainingTypeDocument> trainingTypes) {
        List<TrainingTypeResponse> trainingTypeResponseList = new ArrayList<>();

        for (TrainingTypeDocument trainingTypeDocument : trainingTypes) {
            TrainingTypeResponse trainingTypeResponse = modelMapper
                    .map(trainingTypeDocument, TrainingTypeResponse.class);
            trainingTypeResponseList.add(trainingTypeResponse);
        }

        return trainingTypeResponseList;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PutMapping(
            value = "/{trainingTypeId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TrainingTypeResponse> updateTrainingTypeById(
            @PathVariable final String trainingTypeId,
            @RequestPart(value = "body") final TrainingTypeRequest trainingTypeRequest,
            @RequestPart(value = "image", required = false) final MultipartFile multipartFile
    ) {
        TrainingTypeResponse response = new TrainingTypeResponse();
        try {
            multipartFileValidator.validateBody(trainingTypeRequest);
            imageValidator.isFileSupported(multipartFile);

            TrainingTypeDocument trainingTypeDocument = trainingTypeService
                    .updateTrainingTypeById(trainingTypeId, trainingTypeRequest, multipartFile);

            response = modelMapper.map(trainingTypeDocument, TrainingTypeResponse.class);
            String message = translator.toLocale("training.type.updated");
            response.setMessage(message);
            String imageBase64Encoded = getUpdatedImageAsBase64String(trainingTypeDocument);
            response.setImageBase64Encoded(imageBase64Encoded);

            return ResponseEntity
                    .status(HttpStatus.OK)
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

        } catch (TrainingTypeNotFoundException exception) {
            String reason = translator.toLocale("exception.not.found.training.type");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (DuplicatedTrainingTypeException exception) {
            String reason = translator.toLocale("exception.duplicated.training.type");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @DeleteMapping("/{trainingTypeId}")
    public ResponseEntity<TrainingTypeResponse> removeTrainingTypeById(@PathVariable final String trainingTypeId) {
        try {
            TrainingTypeDocument trainingTypeDocument = trainingTypeService.removeTrainingTypeByName(trainingTypeId);
            TrainingTypeResponse trainingTypeResponse =
                    modelMapper.map(trainingTypeDocument, TrainingTypeResponse.class);

            String message = translator.toLocale("training.type.removed");
            trainingTypeResponse.setMessage(message);

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON).body(trainingTypeResponse);

        } catch (TrainingTypeNotFoundException exception) {
            String reason = translator.toLocale("exception.not.found.training.type");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

}

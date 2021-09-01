package com.healthy.gym.account.controller;

import com.healthy.gym.account.component.ImageValidator;
import com.healthy.gym.account.component.MultipartFileValidator;
import com.healthy.gym.account.component.Translator;
import com.healthy.gym.account.dto.TrainerDTO;
import com.healthy.gym.account.exception.NoUserFound;
import com.healthy.gym.account.pojo.request.TrainerRequest;
import com.healthy.gym.account.pojo.response.TrainerResponse;
import com.healthy.gym.account.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/trainer")
public class TrainerController {

    private static final String REQUEST_FAILURE = "request.failure";
    private final TrainerService trainerService;
    private final Translator translator;
    private final MultipartFileValidator multipartFileValidator;
    private final ImageValidator imageValidator;

    @Autowired
    public TrainerController(
            TrainerService trainerService,
            Translator translator,
            MultipartFileValidator multipartFileValidator,
            ImageValidator imageValidator
    ){
        this.trainerService = trainerService;
        this.translator = translator;
        this.multipartFileValidator = multipartFileValidator;
        this.imageValidator = imageValidator;
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('TRAINER') and principal==#userId) ")
    @PostMapping(
            value = "/{userId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TrainerResponse> createTrainer(
            @PathVariable final String userId,
            @RequestPart(value = "body") final TrainerRequest trainerRequest,
            @RequestPart(value = "image", required = false) final MultipartFile multipartFile
    ) {
        try{
            multipartFileValidator.validateBody(trainerRequest);
            if (multipartFile != null) imageValidator.isFileSupported(multipartFile);

            TrainerDTO trainerDTO = trainerService.createTrainer(userId, trainerRequest, multipartFile);

            String message = translator.toLocale("trainer.created");

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new TrainerResponse(message, trainerDTO));

        } catch (NoUserFound exception) {
            String reason = translator.toLocale("exception.no.user.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(REQUEST_FAILURE);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @GetMapping
    public List<TrainerDTO> getTrainers(){
        try{
            return trainerService.getTrainers();

        } catch (NoUserFound exception) {
            String reason = translator.toLocale("exception.no.user.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(REQUEST_FAILURE);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }


    @GetMapping(value = "/{userId}")
    public TrainerDTO getTrainerById(
            @PathVariable final String userId
    ){
        try{
            return trainerService.getTrainerByUserId(userId);

        } catch (NoUserFound exception) {
            String reason = translator.toLocale("exception.no.user.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(REQUEST_FAILURE);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('TRAINER') and principal==#userId) ")
    @PutMapping(
            value = "/{userId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TrainerResponse> updateTrainer(
            @PathVariable final String userId,
            @RequestPart(value = "body") final TrainerRequest trainerRequest,
            @RequestPart(value = "image", required = false) final MultipartFile multipartFile
    ) {
        try{
            multipartFileValidator.validateBody(trainerRequest);
            if (multipartFile != null) imageValidator.isFileSupported(multipartFile);

            TrainerDTO trainerDTO = trainerService.updateTrainer(userId, trainerRequest, multipartFile);

            String message = translator.toLocale("trainer.updated");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new TrainerResponse(message, trainerDTO));

        } catch (NoUserFound exception) {
            String reason = translator.toLocale("exception.no.user.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(REQUEST_FAILURE);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('TRAINER') and principal==#userId) ")
    @DeleteMapping(
            value = "/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TrainerResponse> deleteTrainer(
            @PathVariable final String userId
    ) {
        try{
            TrainerDTO trainerDTO = trainerService.deleteByUserId(userId);

            String message = translator.toLocale("trainer.removed");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new TrainerResponse(message, trainerDTO));

        } catch (NoUserFound exception) {
            String reason = translator.toLocale("exception.no.user.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(REQUEST_FAILURE);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}

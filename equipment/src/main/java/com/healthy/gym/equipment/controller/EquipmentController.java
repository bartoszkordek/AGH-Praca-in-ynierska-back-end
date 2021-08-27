package com.healthy.gym.equipment.controller;

import com.healthy.gym.equipment.component.ImageValidator;
import com.healthy.gym.equipment.component.MultipartFileValidator;
import com.healthy.gym.equipment.component.Translator;
import com.healthy.gym.equipment.dto.DescriptionDTO;
import com.healthy.gym.equipment.dto.EquipmentDTO;
import com.healthy.gym.equipment.dto.TrainingDTO;
import com.healthy.gym.equipment.exception.DuplicatedEquipmentTypeException;
import com.healthy.gym.equipment.exception.MultipartBodyException;
import com.healthy.gym.equipment.model.request.EquipmentRequest;
import com.healthy.gym.equipment.model.response.EquipmentDTOResponse;
import com.healthy.gym.equipment.service.EquipmentService;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class EquipmentController {

    private static final String EXCEPTION_INTERNAL_ERROR = "exception.internal.error";

    private final EquipmentService equipmentService;
    private final Translator translator;
    private final MultipartFileValidator multipartFileValidator;
    private final ImageValidator imageValidator;
    private final ModelMapper modelMapper;

    @Autowired
    public EquipmentController(
            EquipmentService equipmentService,
            Translator translator,
            MultipartFileValidator multipartFileValidator,
            ImageValidator imageValidator
    ) {
        this.equipmentService = equipmentService;
        this.translator = translator;
        this.multipartFileValidator = multipartFileValidator;
        this.imageValidator = imageValidator;
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping(
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<EquipmentDTOResponse> createTrainingType(
            @RequestPart(value = "body") EquipmentRequest equipmentRequest,
            @RequestPart(value = "image", required = false) MultipartFile multipartFile
    ) {

        try {
            multipartFileValidator.validateBody(equipmentRequest);
            if (multipartFile != null) imageValidator.isFileSupported(multipartFile);

            EquipmentDTO equipmentDTO =
                    equipmentService.createEquipment(equipmentRequest, multipartFile);
            String message = translator.toLocale("equipment.created");

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new EquipmentDTOResponse(message, equipmentDTO));


        } catch (MultipartBodyException exception) {
            String message = translator.toLocale("exception.multipart.body");
            EquipmentDTOResponse response = new EquipmentDTOResponse(message, null);
            response.setErrors(exception.getErrorMap());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);

        } catch (UnsupportedDataTypeException exception) {
            String reason = translator.toLocale("exception.unsupported.data.type");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (DuplicatedEquipmentTypeException exception) {
            String reason = translator.toLocale("exception.duplicated.equipment.type");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @GetMapping
    public List<EquipmentDTO> getEquipments(){

        TrainingDTO training1 = new TrainingDTO();
        training1.setTrainingId(UUID.randomUUID().toString());
        training1.setTitle("Trening indywidualny");
        TrainingDTO training2 = new TrainingDTO();
        training1.setTrainingId(UUID.randomUUID().toString());
        training1.setTitle("Rowery");

        EquipmentDTO equipment1= new EquipmentDTO();
        equipment1.setEquipmentId(UUID.randomUUID().toString());
        equipment1.setTitle("Rower");
        equipment1.setImages(List.of("https://images.morele.net/full/6287257_0_f.jpg"));
        equipment1.setDescription(new DescriptionDTO("Rower", List.of(training1, training2)));

        EquipmentDTO equipment2 = new EquipmentDTO();
        equipment2.setEquipmentId(UUID.randomUUID().toString());
        equipment2.setTitle("Bieżnia");
        equipment2.setImages(List.of("https://www.e-insportline.pl/upload/image/320x320/IMG_79931_stin.jpg"));
        equipment2.setDescription(new DescriptionDTO("Bieżnia", List.of(training1)));

        return List.of(equipment1, equipment2);
    }
}

package com.healthy.gym.equipment.controller;

import com.healthy.gym.equipment.dto.DescriptionDTO;
import com.healthy.gym.equipment.dto.EquipmentDTO;
import com.healthy.gym.equipment.dto.TrainingDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class EquipmentController {

    private static final String INTERNAL_ERROR_EXCEPTION = "exception.internal.error";

    @GetMapping
    public List<EquipmentDTO> getAllEquipment(){

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

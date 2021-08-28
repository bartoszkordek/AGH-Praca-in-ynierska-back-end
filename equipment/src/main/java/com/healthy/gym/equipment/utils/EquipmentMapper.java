package com.healthy.gym.equipment.utils;

import com.healthy.gym.equipment.data.document.EquipmentDocument;
import com.healthy.gym.equipment.data.document.TrainingTypeDocument;
import com.healthy.gym.equipment.dto.DescriptionDTO;
import com.healthy.gym.equipment.dto.EquipmentDTO;
import com.healthy.gym.equipment.dto.TrainingDTO;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

public class EquipmentMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    private EquipmentMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static EquipmentDTO mapEquipmentDocumentToEquipmentDTO(EquipmentDocument equipmentDocument){
        EquipmentDTO equipmentDTO = modelMapper.map(equipmentDocument, EquipmentDTO.class);

        DescriptionDTO descriptionDTO = new DescriptionDTO(
                equipmentDocument.getSynopsis(),
                mapTrainingTypes(equipmentDocument.getTrainings())
        );
        equipmentDTO.setDescription(descriptionDTO);

        return equipmentDTO;
    }

    public static List<EquipmentDTO> mapEquipmentDocumentsToEquipmentDTOs(List<EquipmentDocument> equipmentDocuments){
        List<EquipmentDTO> equipmentDTOs = new ArrayList<>();
        for (EquipmentDocument equipmentDocument : equipmentDocuments){
            EquipmentDTO equipmentDTO = modelMapper.map(equipmentDocument, EquipmentDTO.class);
            DescriptionDTO descriptionDTO = new DescriptionDTO();
            descriptionDTO.setSynopsis(equipmentDocument.getSynopsis());
            descriptionDTO.setTrainings(mapTrainingTypes(equipmentDocument.getTrainings()));
            equipmentDTO.setDescription(descriptionDTO);
            equipmentDTOs.add(equipmentDTO);
        }
        return equipmentDTOs;
    }

    private static List<TrainingDTO> mapTrainingTypes(List<TrainingTypeDocument> trainingTypeDocuments){
        List<TrainingDTO> trainingDTOs = new ArrayList<>();
        for(TrainingTypeDocument trainingTypeDocument : trainingTypeDocuments){
            TrainingDTO trainingDTO = new TrainingDTO();
            trainingDTO.setTrainingId(trainingTypeDocument.getTrainingTypeId());
            trainingDTO.setTitle(trainingTypeDocument.getName());
            trainingDTOs.add(trainingDTO);
        }
        return trainingDTOs;
    }

}

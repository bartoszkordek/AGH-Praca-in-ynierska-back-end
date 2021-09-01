package com.healthy.gym.account.util;

import com.healthy.gym.account.data.document.TrainerDocument;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.dto.DescriptionDTO;
import com.healthy.gym.account.dto.TrainerDTO;
import com.healthy.gym.account.dto.TrainingDTO;
import com.healthy.gym.account.data.document.TrainingTypeDocument;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

public class TrainerMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    private TrainerMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static TrainerDTO mapTrainerDocumentToTrainerDTO(TrainerDocument trainerDocument){
        TrainerDTO trainerDTO = modelMapper.map(trainerDocument, TrainerDTO.class);

        DescriptionDTO descriptionDTO = new DescriptionDTO(
                trainerDocument.getSynopsis(),
                trainerDocument.getFull(),
                mapTrainingTypes(trainerDocument.getTrainingTypeDocuments())
        );
        trainerDTO.setDescription(descriptionDTO);

        String userId = trainerDocument.getUserDocument().getUserId();
        String name = trainerDocument.getUserDocument().getName();
        String surname = trainerDocument.getUserDocument().getSurname();
        String avatar = trainerDocument.getUserDocument().getAvatarUrl();
        trainerDTO.setUserId(userId);
        trainerDTO.setName(name);
        trainerDTO.setSurname(surname);
        trainerDTO.setAvatar(avatar);

        return trainerDTO;
    }

    public static List<TrainerDTO> mapTrainerDocumentsToTrainerDTOs(List<TrainerDocument> trainerDocuments){
        List<TrainerDTO> trainerDTOs = new ArrayList<>();
        for(TrainerDocument trainerDocument : trainerDocuments){
            TrainerDTO trainerDTO = mapTrainerDocumentToTrainerDTO(trainerDocument);
            trainerDTOs.add(trainerDTO);
        }
        return trainerDTOs;
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

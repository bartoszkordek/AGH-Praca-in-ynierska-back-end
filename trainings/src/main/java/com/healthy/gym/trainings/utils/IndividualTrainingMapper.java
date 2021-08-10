package com.healthy.gym.trainings.utils;

import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import org.modelmapper.ModelMapper;

public class IndividualTrainingMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    private IndividualTrainingMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static IndividualTrainingDTO mapIndividualTrainingDocumentToDTO(
            IndividualTrainingDocument individualTrainingDocument
    ) {
        return modelMapper
                .typeMap(IndividualTrainingDocument.class, IndividualTrainingDTO.class)
                .addMapping(source -> source.getLocation().getName(), IndividualTrainingDTO::setLocation)
                .addMapping(source -> source.getTraining().getName(), IndividualTrainingDTO::setTitle)
                .map(individualTrainingDocument);
    }
}

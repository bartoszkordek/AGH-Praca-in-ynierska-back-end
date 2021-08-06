package com.healthy.gym.trainings.utils;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import org.modelmapper.ModelMapper;

public class GroupTrainingMapper {

    private GroupTrainingMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static GroupTrainingDTO mapGroupTrainingsDocumentToDTO(GroupTrainingDocument groupTrainingDocument) {
        ModelMapper modelMapper = new ModelMapper();

        return modelMapper
                .typeMap(GroupTrainingDocument.class, GroupTrainingDTO.class)
                .addMapping(source -> source.getLocation().getName(), GroupTrainingDTO::setLocation)
                .addMapping(source -> source.getTraining().getName(), GroupTrainingDTO::setTitle)
                .map(groupTrainingDocument);
    }
}

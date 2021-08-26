package com.healthy.gym.trainings.utils;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.dto.BasicTrainingDTO;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.dto.GroupTrainingWithoutParticipantsDTO;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class GroupTrainingMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    private GroupTrainingMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static GroupTrainingDTO mapGroupTrainingsDocumentToDTO(GroupTrainingDocument groupTrainingDocument) {
        return modelMapper
                .typeMap(GroupTrainingDocument.class, GroupTrainingDTO.class)
                .addMapping(source -> source.getLocation().getName(), GroupTrainingDTO::setLocation)
                .addMapping(source -> source.getTraining().getName(), GroupTrainingDTO::setTitle)
                .map(groupTrainingDocument);
    }

    public static GroupTrainingWithoutParticipantsDTO mapGroupTrainingsDocumentToDTOWithoutParticipants(
            GroupTrainingDocument groupTrainingDocument
    ) {
        return modelMapper
                .typeMap(GroupTrainingDocument.class, GroupTrainingWithoutParticipantsDTO.class)
                .addMapping(source -> source.getLocation().getName(), GroupTrainingWithoutParticipantsDTO::setLocation)
                .addMapping(source -> source.getTraining().getName(), GroupTrainingWithoutParticipantsDTO::setTitle)
                .map(groupTrainingDocument);
    }

    public static long[] mapGroupTrainingToPairOfStartAndEndDates(GroupTrainingDocument groupTrainingDocument) {
        LocalDateTime startDateTime = groupTrainingDocument.getStartDate();
        LocalDateTime endDateTime = groupTrainingDocument.getEndDate();
        long startDateLong = startDateTime.toEpochSecond(ZoneOffset.UTC);
        long endDateLong = endDateTime.toEpochSecond(ZoneOffset.UTC);
        return new long[]{startDateLong, endDateLong};
    }

    public static BasicTrainingDTO mapGroupTrainingToBasicTrainingDTO(
            GroupTrainingDocument groupTrainingDocument
    ){
        return modelMapper
                .typeMap(GroupTrainingDocument.class, BasicTrainingDTO.class)
                .addMapping(source -> source.getGroupTrainingId(), BasicTrainingDTO::setTrainingId)
                .addMapping(source -> source.getLocation().getName(), BasicTrainingDTO::setLocation)
                .addMapping(source -> source.getTraining().getName(), BasicTrainingDTO::setTitle)
                .map(groupTrainingDocument);
    }
}

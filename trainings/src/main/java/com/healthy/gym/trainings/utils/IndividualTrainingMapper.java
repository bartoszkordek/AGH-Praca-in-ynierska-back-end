package com.healthy.gym.trainings.utils;

import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

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

    public static long[] mapIndividualTrainingToPairOfStartAndEndDate(
            IndividualTrainingDocument individualTrainingDocument
    ) {
        LocalDateTime startDateTime = individualTrainingDocument.getStartDateTime();
        LocalDateTime endDateTime = individualTrainingDocument.getEndDateTime();
        long startDateLong = startDateTime.toEpochSecond(ZoneOffset.UTC);
        long endDateLong = endDateTime.toEpochSecond(ZoneOffset.UTC);
        return new long[]{startDateLong, endDateLong};
    }
}

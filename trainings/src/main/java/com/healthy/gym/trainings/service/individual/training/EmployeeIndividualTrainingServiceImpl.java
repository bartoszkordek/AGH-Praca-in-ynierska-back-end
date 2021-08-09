package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.data.repository.individual.training.IndividualTrainingRepository;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.NoIndividualTrainingFoundException;
import com.healthy.gym.trainings.utils.IndividualTrainingMapper;
import com.healthy.gym.trainings.utils.StartEndDateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.healthy.gym.trainings.utils.IndividualTrainingMapper.mapIndividualTrainingDocumentToDTO;

@Service
public class EmployeeIndividualTrainingServiceImpl implements EmployeeIndividualTrainingService {

    private final IndividualTrainingRepository individualTrainingRepository;

    @Autowired
    public EmployeeIndividualTrainingServiceImpl(IndividualTrainingRepository individualTrainingRepository) {
        this.individualTrainingRepository = individualTrainingRepository;
    }

    @Override
    public List<IndividualTrainingDTO> getIndividualTrainings(
            final String startDate,
            final String endDate,
            final int page,
            final int size
    ) throws StartDateAfterEndDateException, NoIndividualTrainingFoundException {

        StartEndDateValidator validator = new StartEndDateValidator(startDate, endDate);
        LocalDateTime startDateTime = validator.getBeginningOfStartDate();
        LocalDateTime endDateTime = validator.getEndOfEndDate();

        Pageable pageable = PageRequest.of(page, size, Sort.by("startDateTime"));
        Page<IndividualTrainingDocument> individualTrainingDocuments = individualTrainingRepository
                .findAllByStartDateTimeIsAfterAndEndDateTimeIsBefore(startDateTime, endDateTime, pageable);

        List<IndividualTrainingDocument> trainingDocumentList = individualTrainingDocuments.getContent();
        if (trainingDocumentList.isEmpty()) throw new NoIndividualTrainingFoundException();

        return trainingDocumentList
                .stream()
                .map(IndividualTrainingMapper::mapIndividualTrainingDocumentToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IndividualTrainingDTO getIndividualTrainingById(
            final String trainingId
    ) throws NotExistingIndividualTrainingException {

        Optional<IndividualTrainingDocument> training = individualTrainingRepository
                .findByIndividualTrainingId(trainingId);
        if (training.isEmpty()) throw new NotExistingIndividualTrainingException();

        IndividualTrainingDocument trainingDocument = training.get();
        return mapIndividualTrainingDocumentToDTO(trainingDocument);
    }

    @Override
    public List<IndividualTrainingDTO> getAllAcceptedIndividualTrainings(
            final String startDate,
            final String endDate,
            final int page,
            final int size
    ) throws StartDateAfterEndDateException, NoIndividualTrainingFoundException {

        StartEndDateValidator validator = new StartEndDateValidator(startDate, endDate);
        LocalDateTime startDateTime = validator.getBeginningOfStartDate();
        LocalDateTime endDateTime = validator.getEndOfEndDate();

        Pageable pageable = PageRequest.of(page, size, Sort.by("startDateTime"));
        Page<IndividualTrainingDocument> individualTrainingDocuments = individualTrainingRepository
                .findAllByStartDateTimeIsAfterAndEndDateTimeIsBeforeAndAcceptedIsTrue(
                        startDateTime, endDateTime, pageable);

        List<IndividualTrainingDocument> trainingDocumentList = individualTrainingDocuments.getContent();
        if (trainingDocumentList.isEmpty()) throw new NoIndividualTrainingFoundException();

        return trainingDocumentList
                .stream()
                .map(IndividualTrainingMapper::mapIndividualTrainingDocumentToDTO)
                .collect(Collectors.toList());
    }
}

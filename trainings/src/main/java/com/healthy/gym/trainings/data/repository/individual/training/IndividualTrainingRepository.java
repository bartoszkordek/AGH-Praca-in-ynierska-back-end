package com.healthy.gym.trainings.data.repository.individual.training;

import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IndividualTrainingRepository extends MongoRepository<IndividualTrainingDocument, String> {

    Page<IndividualTrainingDocument> findAllByStartDateTimeIsAfterAndEndDateTimeIsBefore(
            LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable
    );

    Optional<IndividualTrainingDocument> findByIndividualTrainingId(String trainingId);

    Page<IndividualTrainingDocument> findAllByStartDateTimeIsAfterAndEndDateTimeIsBeforeAndAcceptedIsTrue(
            LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable
    );

    List<IndividualTrainingDocument> findAllByStartDateTimeIsAfterAndEndDateTimeIsBefore(
            LocalDateTime startDateTime, LocalDateTime endDateTime, Sort sort
    );

    List<IndividualTrainingDocument> findAllByStartDateTimeIsAfterAndEndDateTimeIsBeforeAndCancelledIsFalseAndRejectedIsFalse(
            LocalDateTime startDateTime, LocalDateTime endDateTime, Sort sort
    );
}

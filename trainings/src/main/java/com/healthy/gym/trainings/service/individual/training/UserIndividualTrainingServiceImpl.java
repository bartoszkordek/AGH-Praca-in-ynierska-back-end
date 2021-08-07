package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.data.document.IndividualTrainings;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.IndividualTrainingsRepository;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.exception.NotAuthorizedClientException;
import com.healthy.gym.trainings.exception.RetroIndividualTrainingException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.model.request.IndividualTrainingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class UserIndividualTrainingServiceImpl implements UserIndividualTrainingService {

    private final IndividualTrainingsRepository individualTrainingsRepository;
    private final UserDAO userDAO;

    @Autowired
    public UserIndividualTrainingServiceImpl(
            IndividualTrainingsRepository individualTrainingsRepository,
            UserDAO userDAO
    ) {
        this.individualTrainingsRepository = individualTrainingsRepository;
        this.userDAO = userDAO;
    }

    @Override
    public List<IndividualTrainings> getMyAllTrainings(String clientId) throws UserNotFoundException {
        UserDocument user = userDAO.findByUserId(clientId);
        if (user == null) throw new UserNotFoundException();
        return individualTrainingsRepository.findIndividualTrainingsByClientIdEquals(clientId);
    }

    @Override
    public IndividualTrainings createIndividualTrainingRequest(
            final IndividualTrainingRequest individualTrainingsRequestModel,
            final String clientId
    ) throws InvalidHourException, ParseException, RetroIndividualTrainingException {

        String individualTrainingDate = individualTrainingsRequestModel.getDate();
        String individualTrainingStartTime = individualTrainingsRequestModel.getStartTime();
        if (isTrainingRetroDateAndTime(individualTrainingDate, individualTrainingStartTime)) {
            throw new RetroIndividualTrainingException("Retro date");
        }

        return individualTrainingsRepository.insert(
                new IndividualTrainings(
                        clientId,
                        individualTrainingsRequestModel.getTrainerId(),
                        individualTrainingsRequestModel.getDate(),
                        individualTrainingsRequestModel.getStartTime(),
                        individualTrainingsRequestModel.getEndTime(),
                        -1,
                        individualTrainingsRequestModel.getRemarks(),
                        false,
                        false
                )
        );
    }

    private boolean isTrainingRetroDateAndTime(String date, String startDate) throws ParseException {
        String startDateAndTime = date.concat("-").concat(startDate);
        SimpleDateFormat sdfDateAndTime = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        Date requestDateParsed = sdfDateAndTime.parse(startDateAndTime);

        Date now = new Date();

        return requestDateParsed.before(now);
    }

    @Override
    public IndividualTrainings cancelIndividualTrainingRequest(String trainingId, String clientId)
            throws NotExistingIndividualTrainingException, NotAuthorizedClientException,
            ParseException, RetroIndividualTrainingException {

        IndividualTrainings individualTraining = individualTrainingsRepository
                .findIndividualTrainingsById(trainingId);

        if (individualTraining == null) throw new NotExistingIndividualTrainingException();

        boolean clientIdEquals = individualTrainingsRepository
                .existsIndividualTrainingsByIdAndClientIdEquals(trainingId, clientId);

        if (!clientIdEquals) throw new NotAuthorizedClientException("Training is not authorized by client");

        String individualTrainingDate = individualTraining.getDate();
        String individualTrainingStartTime = individualTraining.getStartTime();
        if (isTrainingRetroDateAndTime(individualTrainingDate, individualTrainingStartTime)) {
            throw new RetroIndividualTrainingException("Retro date");
        }

        individualTrainingsRepository.deleteIndividualTrainingsById(trainingId);
        return individualTraining;
    }
}

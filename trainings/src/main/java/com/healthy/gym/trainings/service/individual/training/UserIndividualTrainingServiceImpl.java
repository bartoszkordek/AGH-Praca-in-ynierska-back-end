package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.data.repository.individual.training.IndividualTrainingRepository;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.model.request.IndividualTrainingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserIndividualTrainingServiceImpl implements UserIndividualTrainingService {

    private final IndividualTrainingRepository individualTrainingRepository;
    private final UserDAO userDAO;

    @Autowired
    public UserIndividualTrainingServiceImpl(
            IndividualTrainingRepository individualTrainingRepository,
            UserDAO userDAO
    ) {
        this.individualTrainingRepository = individualTrainingRepository;
        this.userDAO = userDAO;
    }

    @Override
    public List<IndividualTrainingDTO> getMyAllTrainings(String clientId) throws UserNotFoundException {
//        UserDocument user = userDAO.findByUserId(clientId);
//        if (user == null) throw new UserNotFoundException();
        //return individualTrainingsRepository.findIndividualTrainingsByClientIdEquals(clientId);
        return null;
    }

    @Override
    public IndividualTrainingDTO createIndividualTrainingRequest(
            final IndividualTrainingRequest individualTrainingsRequestModel,
            final String clientId
    ) throws PastDateException, StartDateAfterEndDateException, TrainerOccupiedException,
            TrainerNotFoundException, UserNotFoundException {

//        String individualTrainingDate = individualTrainingsRequestModel.getDate();
//        String individualTrainingStartTime = individualTrainingsRequestModel.getStartDateTime();
//        if (isTrainingRetroDateAndTime(individualTrainingDate, individualTrainingStartTime)) {
//            throw new RetroIndividualTrainingException("Retro date");
//        }

        return null;
//        return individualTrainingsRepository.insert(
//                new IndividualTrainings(
//                        clientId,
//                        individualTrainingsRequestModel.getTrainerId(),
//                        individualTrainingsRequestModel.getDate(),
//                        individualTrainingsRequestModel.getStartTime(),
//                        individualTrainingsRequestModel.getEndTime(),
//                        -1,
//                        individualTrainingsRequestModel.getRemarks(),
//                        false,
//                        false
//                )
//        );
    }

//    private boolean isTrainingRetroDateAndTime(String date, String startDate) throws ParseException {
//        String startDateAndTime = date.concat("-").concat(startDate);
//        SimpleDateFormat sdfDateAndTime = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
//        Date requestDateParsed = sdfDateAndTime.parse(startDateAndTime);
//
//        Date now = new Date();
//
//        return requestDateParsed.before(now);
//    }

    @Override
    public IndividualTrainingDTO cancelIndividualTrainingRequest(String trainingId, String clientId)
            throws NotExistingIndividualTrainingException, UserNotFoundException, PastDateException {

//        IndividualTrainingDocument individualTraining = individualTrainingRepository
//                .findIndividualTrainingsById(trainingId);
//
//        if (individualTraining == null) throw new NotExistingIndividualTrainingException();
//
//        boolean clientIdEquals = individualTrainingRepository
//                .existsIndividualTrainingsByIdAndClientIdEquals(trainingId, clientId);
//
////        if (!clientIdEquals) throw new NotAuthorizedClientException("Training is not authorized by client");
////
////        String individualTrainingDate = individualTraining.getDate();
////        String individualTrainingStartTime = individualTraining.getStartTime();
////        if (isTrainingRetroDateAndTime(individualTrainingDate, individualTrainingStartTime)) {
////            throw new RetroIndividualTrainingException("Retro date");
////        }
//
//        individualTrainingRepository.deleteIndividualTrainingsById(trainingId);
        return null;
    }
}

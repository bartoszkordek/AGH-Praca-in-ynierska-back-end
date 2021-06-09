package com.healthy.gym.trainings.classesToImprove.mock;

public class TrainingsServiceIndividualTrainingsImpl {
//
//    @Autowired
//    IndividualTrainingsDbRepository individualTrainingsDbRepository;
//
//    public TrainingsServiceIndividualTrainingsImpl(IndividualTrainingsDbRepository individualTrainingsDbRepository) {
//        super(individualTrainingsDbRepository);
//    }
//
//
//    private boolean isTrainingRetroDateAndTime(String date, String startDate) throws ParseException {
//        String startDateAndTime = date.concat("-").concat(startDate);
//        SimpleDateFormat sdfDateAndTime = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
//        Date requestDateParsed = sdfDateAndTime.parse(startDateAndTime);
//
//        Date now = new Date();
//
//        if(requestDateParsed.before(now)) return true;
//
//        return false;
//    }
//
//    @Override
//    public IndividualTrainings getIndividualTrainingById(String trainingId) throws NotExistingIndividualTrainingException {
//        if(!individualTrainingsDbRepository.isIndividualTrainingExist(trainingId)){
//            throw new NotExistingIndividualTrainingException("Training with ID: "+ trainingId + " doesn't exist");
//        }
//        return individualTrainingsDbRepository.getIndividualTrainingById(trainingId);
//    }
//
//    @Override
//    public IndividualTrainings createIndividualTrainingRequest(IndividualTrainingRequest individualTrainingsRequestModel,
//                                                               String clientId) throws InvalidHourException, ParseException, RetroIndividualTrainingException {
//        String individualTrainingDate = individualTrainingsRequestModel.getDate();
//        String individualTrainingStartTime = individualTrainingsRequestModel.getStartTime();
//        if(isTrainingRetroDateAndTime(individualTrainingDate ,individualTrainingStartTime)){
//            throw new RetroIndividualTrainingException("Retro date");
//        }
//        return individualTrainingsDbRepository.createIndividualTrainingRequest(individualTrainingsRequestModel, clientId);
//    }
//
//    @Override
//    public IndividualTrainings acceptIndividualTraining(String trainingId, IndividualTrainingAcceptanceRequest individualTrainingsAcceptModel) throws NotExistingIndividualTrainingException, AlreadyAcceptedIndividualTrainingException, HallNoOutOfRangeException, ParseException, RetroIndividualTrainingException {
//        if(!individualTrainingsDbRepository.isIndividualTrainingExist(trainingId)){
//            throw new NotExistingIndividualTrainingException("Training with ID: "+ trainingId + " doesn't exist");
//        }
//        if(individualTrainingsDbRepository.isIndividualTrainingExistAndAccepted(trainingId)){
//            throw new AlreadyAcceptedIndividualTrainingException("Training with ID: "+ trainingId + " has been already accepted");
//        }
//        IndividualTrainings individualTraining = individualTrainingsDbRepository.getIndividualTrainingById(trainingId);
//        String individualTrainingDate = individualTraining.getDate();
//        String individualTrainingStartTime = individualTraining.getStartTime();
//        if(isTrainingRetroDateAndTime(individualTrainingDate ,individualTrainingStartTime)){
//            throw new RetroIndividualTrainingException("Retro date");
//        }
//        if(individualTrainingsAcceptModel.getHallNo() < 0){
//            throw new HallNoOutOfRangeException("Hall no: " + individualTrainingsAcceptModel.getHallNo() + " does not exist");
//        }
//        return individualTrainingsDbRepository.acceptIndividualTrainingRequest(trainingId, individualTrainingsAcceptModel);
//    }
//
//    @Override
//    public IndividualTrainings declineIndividualTraining(String trainingId) throws NotExistingIndividualTrainingException, AlreadyDeclinedIndividualTrainingException {
//        if(!individualTrainingsDbRepository.isIndividualTrainingExist(trainingId)){
//            throw new NotExistingIndividualTrainingException("Training with ID: "+ trainingId + " doesn't exist");
//        }
//        if(individualTrainingsDbRepository.isIndividualTrainingExistAndDeclined(trainingId)){
//            throw new AlreadyDeclinedIndividualTrainingException("Training with ID: "+ trainingId + " has been already declined");
//        }
//        return individualTrainingsDbRepository.declineIndividualTrainingRequest(trainingId);
//    }
//
//    @Override
//    public IndividualTrainings cancelIndividualTrainingRequest(String trainingId, String clientId) throws NotExistingIndividualTrainingException, NotAuthorizedClientException, ParseException, RetroIndividualTrainingException {
//        if (!individualTrainingsDbRepository.isIndividualTrainingExist(trainingId)) {
//            throw new NotExistingIndividualTrainingException("Training with ID: " + trainingId + " doesn't exist");
//        }
//        if (!individualTrainingsDbRepository.isIndividualTrainingExistAndRequestedByClient(trainingId, clientId)) {
//            throw new NotAuthorizedClientException("Training is not authorized by client");
//        }
//
//        IndividualTrainings individualTraining = individualTrainingsDbRepository.getIndividualTrainingById(trainingId);
//        String individualTrainingDate = individualTraining.getDate();
//        String individualTrainingStartTime = individualTraining.getStartTime();
//        System.out.println("individualTrainingDate: " + individualTrainingDate + " individualTrainingStartTime: " +individualTrainingStartTime );
//        if(isTrainingRetroDateAndTime(individualTrainingDate, individualTrainingStartTime)){
//            throw new RetroIndividualTrainingException("Retro date");
//        }
//        return individualTrainingsDbRepository.cancelIndividualTrainingRequest(trainingId);
//    }

}

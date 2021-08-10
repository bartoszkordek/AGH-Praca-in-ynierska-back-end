package com.healthy.gym.trainings.test.utils;

import com.github.javafaker.Faker;
import com.healthy.gym.trainings.data.document.*;
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.enums.GymRole;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.healthy.gym.trainings.utils.GroupTrainingMapper.mapGroupTrainingsDocumentToDTO;

public class TestDocumentUtil {

    private static final Faker faker = new Faker();
    private static final ModelMapper mapper = new ModelMapper();

    private TestDocumentUtil() {
        throw new IllegalStateException("Test utility class.");
    }

    public static TrainingTypeDocument getTestTrainingType() {
        String trainingTypeId = UUID.randomUUID().toString();
        String name = faker.funnyName().name();
        return new TrainingTypeDocument(trainingTypeId, name);
    }

    public static UserDocument getTestUser(String userId) {
        String name = faker.name().firstName();
        String surname = faker.name().lastName();
        String email = faker.internet().emailAddress();
        var roles = List.of(GymRole.USER);
        return new UserDocument(name, surname, email, userId, roles);
    }

    public static UserDocument getTestUser() {
        String userId = UUID.randomUUID().toString();
        return getTestUser(userId);
    }

    public static BasicUserInfoDTO getTestUserDTO() {
        return mapper.map(getTestUser(), BasicUserInfoDTO.class);
    }

    public static UserDocument getTestTrainer() {
        return getTestTrainer(UUID.randomUUID().toString());
    }

    public static UserDocument getTestTrainer(String userId) {
        var user = getTestUser(userId);
        user.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));
        return user;
    }

    public static LocationDocument getTestLocation() {
        String locationId = UUID.randomUUID().toString();
        String name = faker.address().cityName();
        return new LocationDocument(locationId, name);
    }

    public static GroupTrainingDocument getTestGroupTrainingDocument(
            String startDate,
            String endDate,
            UserDocument user,
            boolean isInBasic,
            boolean isInReserve
    ) {
        var basicList = getTestListOfUserDocuments(5);
        if (isInBasic) basicList.add(user);

        var reserveList = getTestListOfUserDocuments(2);
        if (isInReserve) reserveList.add(user);

        String groupTrainingId = UUID.randomUUID().toString();
        TrainingTypeDocument trainingType = getTestTrainingType();
        UserDocument trainer = getTestTrainer();
        LocationDocument location = getTestLocation();

        return getTestGroupTraining(
                getTestTrainingType(),
                List.of(getTestTrainer()),
                getTestLocation(),
                startDate,
                endDate,
                20,
                basicList,
                reserveList
        );
    }

    public static GroupTrainingDTO getTestGroupTrainingDTO(
            String startDate,
            String endDate,
            UserDocument user,
            boolean isInBasic,
            boolean isInReserve
    ) {
        GroupTrainingDocument document =
                getTestGroupTrainingDocument(startDate, endDate, user, isInBasic, isInReserve);
        return mapGroupTrainingsDocumentToDTO(document);
    }

    public static List<UserDocument> getTestListOfUserDocuments(int numberOfUserDocuments) {
        if (numberOfUserDocuments <= 0) {
            throw new IllegalArgumentException("Number of users must be greater than 0.");
        }
        List<UserDocument> list = new ArrayList<>(numberOfUserDocuments);
        for (int i = 0; i < numberOfUserDocuments; i++) {
            list.add(getTestUser());
        }
        return list;
    }

    private static GroupTrainingDocument getTestGroupTraining(
            TrainingTypeDocument trainingTypeDocument,
            List<UserDocument> trainersList,
            LocationDocument locationDocument,
            String startDate,
            String endDate,
            int limit,
            List<UserDocument> basicList,
            List<UserDocument> reserveList
    ) {
        String groupTrainingId = UUID.randomUUID().toString();
        return new GroupTrainingDocument(
                groupTrainingId,
                trainingTypeDocument,
                trainersList,
                LocalDateTime.parse(startDate),
                LocalDateTime.parse(endDate),
                locationDocument,
                limit,
                basicList,
                reserveList
        );
    }

    public static GroupTrainingDocument getTestGroupTraining(String startDate, String endDate) {
        return getTestGroupTraining(
                getTestTrainingType(),
                List.of(getTestTrainer()),
                getTestLocation(),
                startDate,
                endDate,
                10,
                getTestListOfUserDocuments(10),
                getTestListOfUserDocuments(2)
        );
    }

    public static GroupTrainingDocument getTestGroupTraining(
            TrainingTypeDocument savedTrainingType,
            String startDate,
            String endDate
    ) {
        return getTestGroupTraining(
                savedTrainingType,
                List.of(getTestTrainer()),
                getTestLocation(),
                startDate,
                endDate,
                10,
                getTestListOfUserDocuments(10),
                getTestListOfUserDocuments(2)
        );
    }

    public static IndividualTrainingDocument getTestIndividualTraining(
            TrainingTypeDocument trainingTypeDocument,
            List<UserDocument> basicList,
            List<UserDocument> trainersList,
            String startDateTime,
            String endDateTime,
            LocationDocument locationDocument,
            String remarks
    ) {
        String individualTrainingId = UUID.randomUUID().toString();
        return new IndividualTrainingDocument(
                individualTrainingId,
                trainingTypeDocument,
                basicList,
                trainersList,
                LocalDateTime.parse(startDateTime),
                LocalDateTime.parse(endDateTime),
                locationDocument,
                remarks
        );
    }

    public static String getTestRemarks() {
        return faker.lorem().characters(280);
    }

    public static IndividualTrainingDocument getTestIndividualTraining(
            String startDateTime,
            String endDateTime
    ) {
        String individualTrainingId = UUID.randomUUID().toString();
        return new IndividualTrainingDocument(
                individualTrainingId,
                getTestTrainingType(),
                getTestListOfUserDocuments(5),
                List.of(getTestTrainer()),
                LocalDateTime.parse(startDateTime),
                LocalDateTime.parse(endDateTime),
                getTestLocation(),
                getTestRemarks()
        );
    }
}

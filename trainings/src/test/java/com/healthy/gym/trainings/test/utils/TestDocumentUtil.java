package com.healthy.gym.trainings.test.utils;

import com.github.javafaker.Faker;
import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.enums.GymRole;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
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
        var user = getTestUser();
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
        String groupTrainingId = UUID.randomUUID().toString();
        TrainingTypeDocument trainingType = getTestTrainingType();
        UserDocument trainer = getTestTrainer();
        LocationDocument location = getTestLocation();
        UserDocument user1 = getTestUser();
        UserDocument user2 = getTestUser();

        var basicList = isInBasic ? List.of(user1, user2, user) : List.of(user2);
        var reserveList = isInReserve ? List.of(user1, user2, user) : List.of(user2);

        return new GroupTrainingDocument(
                groupTrainingId,
                trainingType,
                List.of(trainer),
                LocalDateTime.parse(startDate),
                LocalDateTime.parse(endDate),
                location,
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

}

package com.healthy.gym.trainings.data.repository.individual.training;

import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UserIndividualTrainingDAOImpl implements UserIndividualTrainingDAO {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserIndividualTrainingDAOImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<IndividualTrainingDocument> findAllIndividualTrainingsWithDatesByUserDocument(
            UserDocument userDocument,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        Criteria criteria = Criteria
                .where("startDateTime").gte(startDateTime)
                .and("endDateTime").lte(endDateTime)
                .and("cancelled").is(false)
                .and("rejected").is(false)
                .and("basicList").in(userDocument);

        return mongoTemplate.find(new Query(criteria), IndividualTrainingDocument.class);
    }

    @Override
    public List<IndividualTrainingDocument> findAllIndividualTrainingsWithStartDateAfterNow(UserDocument userDocument) {
        var now = LocalDateTime.now();
        Criteria criteria = Criteria
                .where("startDateTime").gte(now)
                .and("cancelled").is(false)
                .and("rejected").is(false)
                .and("basicList").in(userDocument);
        return mongoTemplate.find(new Query(criteria), IndividualTrainingDocument.class);
    }
}

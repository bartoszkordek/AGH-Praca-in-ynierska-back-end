package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
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
public class TrainerDAOImpl implements TrainerDAO {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public TrainerDAOImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<GroupTrainingDocument> getTrainerGroupTrainings(
            UserDocument user,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        Criteria criteria = Criteria
                .where("startDate").gte(startDateTime)
                .and("endDate").lte(endDateTime)
                .orOperator(
                        Criteria.where("basicList").in(user),
                        Criteria.where("reserveList").in(user),
                        Criteria.where("trainers").in(user)
                );


        return mongoTemplate.find(new Query(criteria), GroupTrainingDocument.class);
    }

    @Override
    public List<IndividualTrainingDocument> getTrainerIndividualTrainings(
            UserDocument user,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        Criteria criteria = Criteria
                .where("startDateTime").gte(startDateTime)
                .and("endDateTime").lte(endDateTime)
                .orOperator(
                        Criteria.where("basicList").in(user),
                        Criteria.where("trainers").in(user)
                );

        return mongoTemplate.find(new Query(criteria), IndividualTrainingDocument.class);
    }
}

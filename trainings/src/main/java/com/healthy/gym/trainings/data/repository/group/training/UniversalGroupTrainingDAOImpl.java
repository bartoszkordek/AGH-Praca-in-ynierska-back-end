package com.healthy.gym.trainings.data.repository.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UniversalGroupTrainingDAOImpl implements UniversalGroupTrainingDAO {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public UniversalGroupTrainingDAOImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<GroupTrainingDocument> getGroupTrainingDocuments(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        Criteria criteria = Criteria
                .where("startDate").gte(startDateTime)
                .and("endDate").lte(endDateTime);

        return mongoTemplate.find(new Query(criteria), GroupTrainingDocument.class);
    }

    @Override
    public List<GroupTrainingDocument> getGroupTrainingDocumentsByTrainingType(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            TrainingTypeDocument trainingType
    ) {
        Criteria criteria = Criteria
                .where("startDate").gte(startDateTime)
                .and("endDate").lte(endDateTime)
                .and("training").is(trainingType);

        return mongoTemplate.find(new Query(criteria), GroupTrainingDocument.class);
    }
}

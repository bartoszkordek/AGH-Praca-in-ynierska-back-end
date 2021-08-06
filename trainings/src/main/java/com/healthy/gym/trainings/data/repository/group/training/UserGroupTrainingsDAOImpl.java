package com.healthy.gym.trainings.data.repository.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class UserGroupTrainingsDAOImpl implements UserGroupTrainingsDAO {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserGroupTrainingsDAOImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<GroupTrainingDocument> findAllGroupTrainings(
            UserDocument userDocument,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        Criteria criteria = Criteria
                .where("startDate").gte(startDateTime)
                .and("endDate").lte(endDateTime)
                .orOperator(
                        where("basicList").in(userDocument),
                        where("reserveList").in(userDocument)
                );

        return mongoTemplate.find(new Query(criteria), GroupTrainingDocument.class);
    }
}

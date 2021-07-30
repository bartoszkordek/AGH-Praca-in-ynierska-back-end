package com.healthy.gym.trainings.data.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "trainers")
public class TrainerDocument {

    @Id
    private String id;
    private String trainerId;
    private String userId;
}

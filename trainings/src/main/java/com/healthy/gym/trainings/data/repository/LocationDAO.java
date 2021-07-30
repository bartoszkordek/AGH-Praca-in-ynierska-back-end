package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.data.document.LocationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LocationDAO extends MongoRepository<LocationDocument, String> {
    LocationDocument findByLocationId(String locationId);

    LocationDocument findByName(String locationName);
}

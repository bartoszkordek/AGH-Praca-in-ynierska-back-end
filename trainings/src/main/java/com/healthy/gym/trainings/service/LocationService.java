package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.model.request.CreateLocationRequest;
import com.healthy.gym.trainings.shared.LocationDTO;

import java.util.List;

public interface LocationService {
    LocationDTO createLocation(CreateLocationRequest request);

    List<LocationDTO> getAllLocations();

    LocationDTO getLocationById(String id);

    LocationDTO updateLocationById(String id);

    LocationDTO removeLocationById(String id);

}

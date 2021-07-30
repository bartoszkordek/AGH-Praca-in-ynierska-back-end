package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.exception.duplicated.DuplicatedLocationNameException;
import com.healthy.gym.trainings.model.request.CreateLocationRequest;
import com.healthy.gym.trainings.shared.LocationDTO;

import java.util.List;

public interface LocationService {
    LocationDTO createLocation(CreateLocationRequest request) throws DuplicatedLocationNameException;

    List<LocationDTO> getAllLocations();

    LocationDTO getLocationById(String id);

    LocationDTO updateLocationById(String id);

    LocationDTO removeLocationById(String id);

}

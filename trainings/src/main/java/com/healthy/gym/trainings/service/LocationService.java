package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.exception.duplicated.DuplicatedLocationNameException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.model.request.LocationRequest;
import com.healthy.gym.trainings.shared.LocationDTO;

import java.util.List;

public interface LocationService {
    LocationDTO createLocation(LocationRequest request) throws DuplicatedLocationNameException;

    List<LocationDTO> getAllLocations();

    LocationDTO updateLocationById(String id, LocationRequest request)
            throws DuplicatedLocationNameException, LocationNotFoundException;

    LocationDTO removeLocationById(String id) throws LocationNotFoundException;

}

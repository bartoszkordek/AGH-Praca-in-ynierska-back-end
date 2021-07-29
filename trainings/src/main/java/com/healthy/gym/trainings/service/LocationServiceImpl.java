package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.model.request.CreateLocationRequest;
import com.healthy.gym.trainings.shared.LocationDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {
    @Override
    public LocationDTO createLocation(CreateLocationRequest request) {
        return null;
    }

    @Override
    public List<LocationDTO> getAllLocations() {
        return null;
    }

    @Override
    public LocationDTO getLocationById(String id) {
        return null;
    }

    @Override
    public LocationDTO updateLocationById(String id) {
        return null;
    }

    @Override
    public LocationDTO removeLocationById(String id) {
        return null;
    }
}

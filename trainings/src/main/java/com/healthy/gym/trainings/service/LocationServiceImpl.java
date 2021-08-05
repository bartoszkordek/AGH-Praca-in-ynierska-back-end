package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.repository.LocationDAO;
import com.healthy.gym.trainings.exception.duplicated.DuplicatedLocationNameException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.model.request.LocationRequest;
import com.healthy.gym.trainings.dto.LocationDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationDAO locationDAO;
    private final ModelMapper modelMapper;

    @Autowired
    public LocationServiceImpl(LocationDAO locationDAO) {
        this.locationDAO = locationDAO;
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public LocationDTO createLocation(LocationRequest request) throws DuplicatedLocationNameException {
        String name = request.getName();
        LocationDocument locationDocument = locationDAO.findByName(name);
        if (locationDocument != null) throw new DuplicatedLocationNameException();
        LocationDocument locationToSave = new LocationDocument(
                UUID.randomUUID().toString(),
                request.getName()
        );
        LocationDocument locationSaved = locationDAO.save(locationToSave);
        return modelMapper.map(locationSaved, LocationDTO.class);
    }

    @Override
    public List<LocationDTO> getAllLocations() {
        List<LocationDocument> locationDocuments = locationDAO.findAll();
        return locationDocuments
                .stream()
                .map(locationDocument -> modelMapper.map(locationDocument, LocationDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public LocationDTO updateLocationById(String id, LocationRequest request)
            throws LocationNotFoundException, DuplicatedLocationNameException {

        LocationDocument locationDocument = locationDAO.findByLocationId(id);
        if (locationDocument == null) throw new LocationNotFoundException();

        String nameToChange = request.getName();

        LocationDocument duplicatedLocationDocument = locationDAO.findByName(nameToChange);
        if (duplicatedLocationDocument != null) throw new DuplicatedLocationNameException();

        locationDocument.setName(nameToChange);
        LocationDocument updateDLocationDocument = locationDAO.save(locationDocument);

        return modelMapper.map(updateDLocationDocument, LocationDTO.class);
    }

    @Override
    public LocationDTO removeLocationById(String id) throws LocationNotFoundException {
        LocationDocument locationDocument = locationDAO.findByLocationId(id);
        if (locationDocument == null) throw new LocationNotFoundException();
        locationDAO.delete(locationDocument);
        return modelMapper.map(locationDocument, LocationDTO.class);
    }
}

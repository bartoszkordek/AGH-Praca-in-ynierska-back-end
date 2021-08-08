package com.healthy.gym.gympass.service;

import com.healthy.gym.gympass.data.document.GymPassDocument;
import com.healthy.gym.gympass.data.repository.GymPassOfferDAO;
import com.healthy.gym.gympass.dto.GymPassDTO;
import com.healthy.gym.gympass.exception.DuplicatedOffersException;
import com.healthy.gym.gympass.exception.GymPassNotFoundException;
import com.healthy.gym.gympass.exception.NoOffersException;
import com.healthy.gym.gympass.pojo.request.GymPassOfferRequest;
import com.healthy.gym.gympass.shared.Description;
import com.healthy.gym.gympass.shared.Price;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OfferServiceImpl implements OfferService{

    private final GymPassOfferDAO gymPassOfferDAO;
    private final ModelMapper modelMapper;

    @Autowired
    public OfferServiceImpl(
            GymPassOfferDAO gymPassOfferDAO
    ){
        this.gymPassOfferDAO = gymPassOfferDAO;
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public List<GymPassDTO> getGymPassOffers()
            throws NoOffersException {

        List<GymPassDocument> gymPassOfferDocuments = gymPassOfferDAO.findAll();

        if(gymPassOfferDocuments.isEmpty()) throw new NoOffersException("No offers");

        return gymPassOfferDocuments
                .stream()
                .map(gymPassDocument -> modelMapper.map(gymPassDocument, GymPassDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public GymPassDTO createGymPassOffer(GymPassOfferRequest request)
            throws DuplicatedOffersException {

        String requestTitle = request.getTitle();
        if(gymPassOfferDAO.findByTitle(requestTitle) != null)
            throw new DuplicatedOffersException("Offer with the same title already exists");

        GymPassDocument gymPassDocumentToSave = new GymPassDocument(
                UUID.randomUUID().toString(),
                request.getTitle(),
                request.getSubheader(),
                new Price(
                        request.getAmount(),
                        request.getCurrency(),
                        request.getPeriod()
                ),
                request.isPremium(),
                new Description(request.getSynopsis(), request.getFeatures())
        );
        GymPassDocument gymPassDocumentSaved = gymPassOfferDAO.save(gymPassDocumentToSave);
        return modelMapper.map(gymPassDocumentSaved, GymPassDTO.class);
    }

    @Override
    public GymPassDTO updateGymPassOffer(String id, GymPassOfferRequest request)
            throws DuplicatedOffersException, GymPassNotFoundException {

        GymPassDocument gymPassDocument = gymPassOfferDAO.findByDocumentId(id);
        if(gymPassDocument == null)
            throw new GymPassNotFoundException("Offer does not exist");

        String requestTitle = request.getTitle();
        GymPassDocument documentWithRequestTitle = gymPassOfferDAO.findByTitle(requestTitle);

        if(documentWithRequestTitle != null && !documentWithRequestTitle.getDocumentId().equals(id))
            throw new DuplicatedOffersException("Offer with the same title already exists");

        gymPassDocument.setTitle(requestTitle);
        gymPassDocument.setSubheader(request.getSubheader());
        gymPassDocument.setPrice(
                new Price(request.getAmount(),
                        request.getCurrency(),
                        request.getPeriod()
                )
        );
        gymPassDocument.setPremium(request.isPremium());
        gymPassDocument.setDescription(
                new Description(
                     request.getSynopsis(),
                     request.getFeatures()
                )
        );

        GymPassDocument updatedGymPassDocument = gymPassOfferDAO.save(gymPassDocument);

        return modelMapper.map(updatedGymPassDocument, GymPassDTO.class);
    }

    @Override
    public GymPassDTO deleteGymPassOffer(String id) throws GymPassNotFoundException {

        GymPassDocument gymPassDocument = gymPassOfferDAO.findByDocumentId(id);
        if(gymPassDocument == null)
            throw new GymPassNotFoundException("Offer does not exist");
        gymPassOfferDAO.delete(gymPassDocument);

        return modelMapper.map(gymPassDocument, GymPassDTO.class);
    }
}

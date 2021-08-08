package com.healthy.gym.gympass.service;

import com.healthy.gym.gympass.data.document.GymPassDocument;
import com.healthy.gym.gympass.data.document.PurchasedGymPassDocument;
import com.healthy.gym.gympass.data.document.UserDocument;
import com.healthy.gym.gympass.data.repository.GymPassOfferDAO;
import com.healthy.gym.gympass.data.repository.PurchasedGymPassDAO;
import com.healthy.gym.gympass.data.repository.UserDAO;
import com.healthy.gym.gympass.dto.PurchasedGymPassDTO;
import com.healthy.gym.gympass.exception.*;
import com.healthy.gym.gympass.pojo.request.PurchasedGymPassRequest;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class PurchaseServiceImpl implements PurchaseService{

    private final PurchasedGymPassDAO purchasedGymPassDAO;
    private final GymPassOfferDAO gymPassOfferDAO;
    private final UserDAO userDAO;
    private final ModelMapper modelMapper;

    private static final String MAX_END_DATE = "9999-12-31";

    @Autowired
    public PurchaseServiceImpl(
            PurchasedGymPassDAO purchasedGymPassDAO,
            GymPassOfferDAO gymPassOfferDAO,
            UserDAO userDAO
    ){
        this.purchasedGymPassDAO = purchasedGymPassDAO;
        this.gymPassOfferDAO = gymPassOfferDAO;
        this.userDAO = userDAO;
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public PurchasedGymPassDTO purchaseGymPass(PurchasedGymPassRequest request)
            throws OfferNotFoundException, UserNotFoundException, RetroPurchasedException,
            StartDateAfterEndDateException, NotSpecifiedGymPassTypeException{

        String gymPassOfferId = request.getGymPassOfferId();
        GymPassDocument gymPassOfferDocument = gymPassOfferDAO.findByDocumentId(gymPassOfferId);
        if(gymPassOfferDocument == null) throw new OfferNotFoundException("Gympass offer not exist");

        String userId = request.getUserId();
        UserDocument userDocument = userDAO.findByUserId(userId);
        if(userDocument == null) throw  new UserNotFoundException("User not exist");

        String startDate = request.getStartDate();
        String endDate = request.getEndDate();
        LocalDate parsedStartDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate parsedEndDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        if(parsedStartDate.isBefore(LocalDate.now()) || parsedEndDate.isBefore(LocalDate.now()))
            throw new RetroPurchasedException("Cannot buy gympass with retro date");
        if(parsedStartDate.isAfter(parsedEndDate))
            throw new StartDateAfterEndDateException("Start date after end date");

        int entries = request.getEntries();
        if(endDate.equals(MAX_END_DATE) && entries == Integer.MAX_VALUE)
            throw new NotSpecifiedGymPassTypeException("Not specified gympass type");

        PurchasedGymPassDocument purchasedGymPassDocumentToSave = new PurchasedGymPassDocument(
                UUID.randomUUID().toString(),
                gymPassOfferDocument,
                userDocument,
                LocalDateTime.now(),
                parsedStartDate,
                parsedEndDate,
                entries
        );
        PurchasedGymPassDocument purchasedGymPassDocumentSaved = purchasedGymPassDAO.save(purchasedGymPassDocumentToSave);
        return modelMapper.map(purchasedGymPassDocumentSaved, PurchasedGymPassDTO.class);

    }

    @Override
    public PurchasedGymPassDTO suspendGymPass(String individualGymPassId, String date)
            throws GymPassNotFoundException, RetroSuspensionDate {

        PurchasedGymPassDocument purchasedGymPassDocument = purchasedGymPassDAO.findByPurchasedGymPassDocumentId(individualGymPassId);
        if(purchasedGymPassDocument == null) throw new GymPassNotFoundException("Gympass with current ID does not exist");

        LocalDate now = LocalDate.now();
        LocalDate suspensionDate = now;
        if(date != null){
            suspensionDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            if(suspensionDate.isBefore(now) || suspensionDate.isEqual(now))
                throw new RetroSuspensionDate("Retro suspension date");
        }

        purchasedGymPassDocument.setSuspensionDate(suspensionDate);
        PurchasedGymPassDocument purchasedGymPassDocumentSaved = purchasedGymPassDAO.save(purchasedGymPassDocument);
        return modelMapper.map(purchasedGymPassDocumentSaved, PurchasedGymPassDTO.class);
    }
}

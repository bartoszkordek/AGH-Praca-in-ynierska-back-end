package com.healthy.gym.gympass.service;

import com.healthy.gym.gympass.dto.PurchasedGymPassDTO;
import com.healthy.gym.gympass.dto.PurchasedGymPassStatusValidationResultDTO;
import com.healthy.gym.gympass.dto.PurchasedUserGymPassDTO;
import com.healthy.gym.gympass.exception.*;
import com.healthy.gym.gympass.pojo.request.PurchasedGymPassRequest;

import java.util.List;

public interface PurchaseService {

    PurchasedGymPassDTO purchaseGymPass(PurchasedGymPassRequest request)
            throws OfferNotFoundException, UserNotFoundException, RetroPurchasedException,
            StartDateAfterEndDateException, NotSpecifiedGymPassTypeException;

    PurchasedGymPassDTO suspendGymPass(String individualGymPassId, String suspensionDate)
            throws GymPassNotFoundException, AlreadySuspendedGymPassException, RetroSuspensionDateException,
            SuspensionDateAfterEndDateException;

    PurchasedGymPassStatusValidationResultDTO checkGymPassValidityStatus(String individualGymPassId)
            throws GymPassNotFoundException;

    List<PurchasedUserGymPassDTO> getUserGymPasses(String userId, String startDate, String endDate)
        throws UserNotFoundException, StartDateAfterEndDateException, NoGymPassesException;

    PurchasedGymPassDTO deleteGymPass(String individualGymPassId)
            throws GymPassNotFoundException;
}

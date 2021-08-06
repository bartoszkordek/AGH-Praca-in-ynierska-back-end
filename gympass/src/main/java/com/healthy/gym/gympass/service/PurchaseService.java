package com.healthy.gym.gympass.service;

import com.healthy.gym.gympass.dto.PurchasedGymPassDTO;
import com.healthy.gym.gympass.exception.OfferNotFoundException;
import com.healthy.gym.gympass.exception.RetroPurchasedException;
import com.healthy.gym.gympass.exception.UserNotFoundException;
import com.healthy.gym.gympass.pojo.request.PurchasedGymPassRequest;

public interface PurchaseService {

    PurchasedGymPassDTO purchaseGymPass(PurchasedGymPassRequest request)
            throws OfferNotFoundException, UserNotFoundException, RetroPurchasedException;

}

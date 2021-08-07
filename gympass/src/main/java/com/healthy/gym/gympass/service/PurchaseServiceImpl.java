package com.healthy.gym.gympass.service;

import com.healthy.gym.gympass.dto.PurchasedGymPassDTO;
import com.healthy.gym.gympass.exception.NotSpecifiedGymPassTypeException;
import com.healthy.gym.gympass.exception.OfferNotFoundException;
import com.healthy.gym.gympass.exception.RetroPurchasedException;
import com.healthy.gym.gympass.exception.UserNotFoundException;
import com.healthy.gym.gympass.pojo.request.PurchasedGymPassRequest;
import org.springframework.stereotype.Service;

@Service
public class PurchaseServiceImpl implements PurchaseService{
    @Override
    public PurchasedGymPassDTO purchaseGymPass(PurchasedGymPassRequest request)
            throws OfferNotFoundException, UserNotFoundException, RetroPurchasedException,
            NotSpecifiedGymPassTypeException{
        return null;
    }
}

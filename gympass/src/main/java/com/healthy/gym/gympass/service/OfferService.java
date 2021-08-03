package com.healthy.gym.gympass.service;

import com.healthy.gym.gympass.dto.GymPassDTO;
import com.healthy.gym.gympass.exception.NoOffersException;
import com.healthy.gym.gympass.pojo.request.GymPassOfferRequest;

import java.util.List;

public interface OfferService {

    List<GymPassDTO> getGymPassOffer() throws NoOffersException;

    GymPassDTO createGymPassOffer(GymPassOfferRequest request);

}

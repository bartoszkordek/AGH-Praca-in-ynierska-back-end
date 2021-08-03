package com.healthy.gym.gympass.service;

import com.healthy.gym.gympass.dto.GymPassDTO;
import com.healthy.gym.gympass.exception.NoOffersException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfferServiceImpl implements OfferService{

    @Override
    public List<GymPassDTO> getGymPassOffer() throws NoOffersException {
        return null;
    }

    @Override
    public GymPassDTO createGymPassOffer() {
        return null;
    }
}

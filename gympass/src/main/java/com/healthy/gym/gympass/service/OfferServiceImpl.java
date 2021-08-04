package com.healthy.gym.gympass.service;

import com.healthy.gym.gympass.dto.GymPassDTO;
import com.healthy.gym.gympass.exception.DuplicatedOffersException;
import com.healthy.gym.gympass.exception.NoOffersException;
import com.healthy.gym.gympass.pojo.request.GymPassOfferRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfferServiceImpl implements OfferService{

    @Override
    public List<GymPassDTO> getGymPassOffer() throws NoOffersException {
        return null;
    }

    @Override
    public GymPassDTO createGymPassOffer(GymPassOfferRequest request) throws DuplicatedOffersException {
        return null;
    }
}

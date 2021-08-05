package com.healthy.gym.gympass.service;

import com.healthy.gym.gympass.data.repository.GymPassOfferDAO;
import com.healthy.gym.gympass.dto.GymPassDTO;
import com.healthy.gym.gympass.exception.DuplicatedOffersException;
import com.healthy.gym.gympass.exception.InvalidGymPassOfferId;
import com.healthy.gym.gympass.exception.NoOffersException;
import com.healthy.gym.gympass.pojo.request.GymPassOfferRequest;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<GymPassDTO> getGymPassOffer()
            throws NoOffersException {
        return null;
    }

    @Override
    public GymPassDTO createGymPassOffer(GymPassOfferRequest request)
            throws DuplicatedOffersException {
        return null;
    }

    @Override
    public GymPassDTO updateGymPassOffer(String id, GymPassOfferRequest request)
            throws DuplicatedOffersException, InvalidGymPassOfferId {
        return null;
    }

    @Override
    public GymPassDTO deleteGymPassOffer(String id) throws InvalidGymPassOfferId {
        return null;
    }
}

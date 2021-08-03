package com.healthy.gym.gympass.pojo.response;

import com.healthy.gym.gympass.dto.GymPassDTO;

import java.util.List;

public class GymPassOfferResponse extends AbstractResponse {
    private GymPassDTO gymPassDTO;

    public GymPassOfferResponse() {
    }

    public GymPassOfferResponse(String message, GymPassDTO gymPassDTO) {
        super(message);
        this.gymPassDTO = gymPassDTO;
    }
}

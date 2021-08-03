package com.healthy.gym.gympass.pojo.response;

import com.healthy.gym.gympass.dto.GymPassDTO;

import java.util.List;

public class GymPassOfferResponse extends AbstractResponse {
    private GymPassDTO gymPass;

    public GymPassOfferResponse() {
    }

    public GymPassOfferResponse(String message, GymPassDTO gymPass) {
        super(message);
        this.gymPass = gymPass;
    }
}

package com.healthy.gym.gympass.pojo.response;

import com.healthy.gym.gympass.data.document.GymPassDocument;

import java.util.List;

public class GymPassOfferResponse extends AbstractResponse {
    private List<GymPassDocument> gymPassDocument;

    public GymPassOfferResponse() {
    }

    public GymPassOfferResponse(String message, List<GymPassDocument> gymPassDocument) {
        super(message);
        this.gymPassDocument = gymPassDocument;
    }
}

package com.healthy.gym.gympass.pojo.response;

import com.healthy.gym.gympass.dto.GymPassDTO;

import java.util.Objects;

public class GymPassOfferResponse extends AbstractResponse {
    private GymPassDTO gymPass;

    public GymPassOfferResponse() {
    }

    public GymPassOfferResponse(String message, GymPassDTO gymPass) {
        super(message);
        this.gymPass = gymPass;
    }

    public GymPassDTO getGymPass() {
        return gymPass;
    }

    public void setGymPass(GymPassDTO gymPass) {
        this.gymPass = gymPass;
    }

    @Override
    public String toString() {
        return "GymPassOfferResponse{" +
                "gymPass=" + gymPass +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GymPassOfferResponse that = (GymPassOfferResponse) o;
        return Objects.equals(gymPass, that.gymPass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gymPass);
    }
}

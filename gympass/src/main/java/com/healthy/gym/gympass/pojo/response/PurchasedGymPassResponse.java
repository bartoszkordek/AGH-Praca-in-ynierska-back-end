package com.healthy.gym.gympass.pojo.response;

import com.healthy.gym.gympass.dto.PurchasedGymPassDTO;

import java.util.Objects;

public class PurchasedGymPassResponse extends AbstractResponse{

    private PurchasedGymPassDTO purchasedGymPass;

    public PurchasedGymPassResponse() {
    }

    public PurchasedGymPassResponse(String message, PurchasedGymPassDTO purchasedGymPass) {
        super(message);
        this.purchasedGymPass = purchasedGymPass;
    }

    public PurchasedGymPassDTO getPurchasedGymPass() {
        return purchasedGymPass;
    }

    public void setPurchasedGymPass(PurchasedGymPassDTO purchasedGymPass) {
        this.purchasedGymPass = purchasedGymPass;
    }

    @Override
    public String toString() {
        return "PurchasedGymPassResponse{" +
                "purchasedGymPass=" + purchasedGymPass +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PurchasedGymPassResponse that = (PurchasedGymPassResponse) o;
        return Objects.equals(purchasedGymPass, that.purchasedGymPass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), purchasedGymPass);
    }
}

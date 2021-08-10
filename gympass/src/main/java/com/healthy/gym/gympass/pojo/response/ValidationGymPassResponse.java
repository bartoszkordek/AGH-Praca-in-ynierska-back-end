package com.healthy.gym.gympass.pojo.response;

import com.healthy.gym.gympass.dto.PurchasedGymPassStatusValidationResultDTO;

import java.util.Objects;

public class ValidationGymPassResponse extends AbstractResponse{

    private PurchasedGymPassStatusValidationResultDTO result;

    public ValidationGymPassResponse() {
    }

    public ValidationGymPassResponse(String message, PurchasedGymPassStatusValidationResultDTO result) {
        super(message);
        this.result = result;
    }

    public PurchasedGymPassStatusValidationResultDTO getResult() {
        return result;
    }

    public void setResult(PurchasedGymPassStatusValidationResultDTO result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ValidationGymPassResponse{" +
                "result=" + result +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ValidationGymPassResponse that = (ValidationGymPassResponse) o;
        return Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                result
        );
    }
}

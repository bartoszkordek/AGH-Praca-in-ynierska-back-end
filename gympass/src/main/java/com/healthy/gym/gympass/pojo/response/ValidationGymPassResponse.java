package com.healthy.gym.gympass.pojo.response;

import java.util.Objects;

public class ValidationGymPassResponse extends AbstractResponse{

    private boolean valid;

    public ValidationGymPassResponse() {
    }

    public ValidationGymPassResponse(String message, boolean valid) {
        super(message);
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "ValidationGymPassResponse{" +
                "valid=" + valid +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ValidationGymPassResponse that = (ValidationGymPassResponse) o;
        return valid == that.valid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                valid
        );
    }
}

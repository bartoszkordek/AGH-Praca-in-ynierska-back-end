package com.healthy.gym.account.pojo.response;

import com.healthy.gym.account.dto.TrainerDTO;

import java.util.Objects;

public class TrainerResponse extends AbstractResponse{

    private TrainerDTO trainer;

    public TrainerResponse() {}

    public TrainerResponse(
            String message,
            TrainerDTO trainer
    ) {
        super(message);
        this.trainer = trainer;
    }

    public TrainerDTO getTrainer() {
        return trainer;
    }

    public void setTrainer(TrainerDTO trainer) {
        this.trainer = trainer;
    }

    @Override
    public String toString() {
        return "TrainerResponse{" +
                "trainer=" + trainer +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TrainerResponse that = (TrainerResponse) o;
        return Objects.equals(trainer, that.trainer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), trainer);
    }
}

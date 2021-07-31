package com.healthy.gym.trainings.model.request;

import java.util.Objects;

public class IndividualTrainingAcceptanceRequest {

    private int hallNo;

    public IndividualTrainingAcceptanceRequest() {
    }

    public IndividualTrainingAcceptanceRequest(int hallNo) {
        this.hallNo = hallNo;
    }

    public int getHallNo() {
        return hallNo;
    }

    public void setHallNo(int hallNo) {
        this.hallNo = hallNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndividualTrainingAcceptanceRequest that = (IndividualTrainingAcceptanceRequest) o;
        return hallNo == that.hallNo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hallNo);
    }

    @Override
    public String toString() {
        return "IndividualTrainingAcceptanceRequest{" +
                "hallNo=" + hallNo +
                '}';
    }
}

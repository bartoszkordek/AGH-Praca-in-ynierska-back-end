package com.healthy.gym.trainings.shared;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParticipantsDTO {
    private List<BasicUserInfoDTO> basicList;
    private List<BasicUserInfoDTO> reservationList;

    public ParticipantsDTO() {
    }

    public ParticipantsDTO(List<BasicUserInfoDTO> basicList, List<BasicUserInfoDTO> reservationList) {
        this.basicList = basicList;
        this.reservationList = reservationList;
    }

    public List<BasicUserInfoDTO> getBasicList() {
        return basicList;
    }

    public void setBasicList(List<BasicUserInfoDTO> basicList) {
        this.basicList = basicList;
    }

    public List<BasicUserInfoDTO> getReservationList() {
        return reservationList;
    }

    public void setReservationList(List<BasicUserInfoDTO> reservationList) {
        this.reservationList = reservationList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantsDTO that = (ParticipantsDTO) o;
        return Objects.equals(basicList, that.basicList)
                && Objects.equals(reservationList, that.reservationList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(basicList, reservationList);
    }

    @Override
    public String toString() {
        return "ParticipantsDTO{" +
                "basicList=" + basicList +
                ", reservationList=" + reservationList +
                '}';
    }
}

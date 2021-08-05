package com.healthy.gym.trainings.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParticipantsDTO {
    private List<BasicUserInfoDTO> basicList;
    private List<BasicUserInfoDTO> reserveList;

    public ParticipantsDTO() {
        this.basicList = new ArrayList<>();
        this.reserveList = new ArrayList<>();
    }

    public ParticipantsDTO(List<BasicUserInfoDTO> basicList, List<BasicUserInfoDTO> reservationList) {
        this.basicList = basicList;
        this.reserveList = reservationList;
    }

    public List<BasicUserInfoDTO> getBasicList() {
        return basicList;
    }

    public void setBasicList(List<BasicUserInfoDTO> basicList) {
        this.basicList = basicList;
    }

    public List<BasicUserInfoDTO> getReserveList() {
        return reserveList;
    }

    public void setReserveList(List<BasicUserInfoDTO> reserveList) {
        this.reserveList = reserveList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantsDTO that = (ParticipantsDTO) o;
        return Objects.equals(basicList, that.basicList)
                && Objects.equals(reserveList, that.reserveList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(basicList, reserveList);
    }

    @Override
    public String toString() {
        return "ParticipantsDTO{" +
                "basicList=" + basicList +
                ", reserveList=" + reserveList +
                '}';
    }
}

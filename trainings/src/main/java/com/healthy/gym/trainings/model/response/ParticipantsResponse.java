package com.healthy.gym.trainings.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;

public class ParticipantsResponse {

    @NotNull
    @JsonProperty("userId")
    private String userId;

    @NotNull
    @JsonProperty("name")
    private String name;

    @NotNull
    @JsonProperty("surname")
    private String surname;

    //TODO ADD Avatar

    public ParticipantsResponse(@JsonProperty("userId") String userId,
                                @JsonProperty("name") String name,
                                @JsonProperty("surname") String surname){
        this.userId = userId;
        this.name = name;
        this.surname = surname;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
}

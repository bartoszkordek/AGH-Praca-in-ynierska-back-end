package com.healthy.gym.trainings.model.response;

import com.healthy.gym.trainings.dto.LocationDTO;

import java.util.Objects;

public class LocationResponse extends AbstractResponse {
    private LocationDTO location;

    public LocationResponse() {
    }

    public LocationResponse(LocationDTO location) {
        this.location = location;
    }

    public LocationResponse(String message, LocationDTO location) {
        super(message);
        this.location = location;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LocationResponse that = (LocationResponse) o;
        return Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), location);
    }

    @Override
    public String toString() {
        return "LocationResponse{" +
                "location=" + location +
                "} " + super.toString();
    }
}

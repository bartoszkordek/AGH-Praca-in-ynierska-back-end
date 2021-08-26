package com.healthy.gym.trainings.dto;

import java.util.Objects;

public class LocationDTO {

    private String locationId;
    private String name;

    public LocationDTO() {
    }

    public LocationDTO(String id, String name) {
        this.locationId = id;
        this.name = name;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationDTO that = (LocationDTO) o;
        return Objects.equals(locationId, that.locationId) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationId, name);
    }

    @Override
    public String toString() {
        return "LocationDTO{" +
                "id='" + locationId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

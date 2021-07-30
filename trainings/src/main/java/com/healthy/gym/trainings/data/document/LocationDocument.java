package com.healthy.gym.trainings.data.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "locations")
public class LocationDocument {

    @Id
    private String id;
    private String locationId;
    private String name;

    public LocationDocument() {
        //empty constructor required by spring data mapper
    }

    public LocationDocument(String locationId, String name) {
        this.locationId = locationId;
        this.name = name;
    }

    public LocationDocument(String id, String locationId, String name) {
        this.id = id;
        this.locationId = locationId;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        LocationDocument that = (LocationDocument) o;
        return Objects.equals(id, that.id)
                && Objects.equals(locationId, that.locationId)
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, locationId, name);
    }

    @Override
    public String toString() {
        return "LocationDocument{" +
                "id='" + id + '\'' +
                ", locationId='" + locationId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

package com.healthy.gym.gympass.shared;

import java.util.List;
import java.util.Objects;

public class Description {
    private String synopsis;
    private List<String> features;

    public Description() {
    }

    public Description(String synopsis, List<String> features) {
        this.synopsis = synopsis;
        this.features = features;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Description that = (Description) o;
        return Objects.equals(synopsis, that.synopsis)
                && Objects.equals(features, that.features);
    }

    @Override
    public int hashCode() {
        return Objects.hash(synopsis, features);
    }

    @Override
    public String toString() {
        return "Description{" +
                "synopsis='" + synopsis + '\'' +
                ", features=" + features +
                '}';
    }
}

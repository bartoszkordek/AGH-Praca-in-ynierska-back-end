package com.healthy.gym.trainings.exception.occupied;

public class LocationOccupiedException extends Exception {
    public LocationOccupiedException() {
    }

    public LocationOccupiedException(String message) {
        super(message);
    }
}

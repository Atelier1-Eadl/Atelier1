package com.smartcity.iot.exception;

public class SensorAlreadyExistsException extends RuntimeException {

    public SensorAlreadyExistsException(String externalId) {
        super("Un capteur avec l'identifiant externe '" + externalId + "' existe deja.");
    }
}

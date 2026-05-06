package com.smartcity.iot.exception;

import java.util.UUID;

public class SensorNotFoundException extends RuntimeException {

    public SensorNotFoundException(UUID id) {
        super("Capteur introuvable avec l'identifiant : " + id);
    }

    public SensorNotFoundException(String externalId) {
        super("Capteur introuvable avec l'identifiant externe : " + externalId);
    }
}

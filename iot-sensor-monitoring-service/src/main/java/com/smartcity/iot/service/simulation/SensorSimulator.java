package com.smartcity.iot.service.simulation;

import com.smartcity.iot.entity.Sensor;

/**
 * Generic contract for all sensor simulators.
 * T is the reading entity type produced by this simulator.
 */
public interface SensorSimulator<T> {

    T generateReading(Sensor sensor);

    void applyIncident(String incidentType, int durationSeconds);

    void clearIncident();

    boolean hasActiveIncident();
}

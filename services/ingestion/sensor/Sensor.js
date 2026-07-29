const ActiveState = require("./ActiveState");
const InactiveState = require("./InactiveState");
const FaultyState = require("./FaultyState");

class Sensor {
  constructor(sensorId, stationId, zoneId) {
    // ENCAPSULATION
    this.sensorId = sensorId;
    this.stationId = stationId;
    this.zoneId = zoneId;

    // état initial
    this.state = new InactiveState();
  }

  // ===== STATE BEHAVIOR =====

  canSendMeasurement() {
    return this.state.canSendMeasurement();
  }

  getStatus() {
    return this.state.getName();
  }

  // ===== STATE TRANSITIONS =====

  activate() {
    this.state = new ActiveState();
  }

  deactivate() {
    this.state = new InactiveState();
  }

  markAsFaulty() {
    this.state = new FaultyState();
  }

  // ===== GETTERS (ENCAPSULATION) =====

  getSensorId() {
    return this.sensorId;
  }

  getStationId() {
    return this.stationId;
  }

  getZoneId() {
    return this.zoneId;
  }
}

module.exports = Sensor;
const SensorState = require("./SensorState");

class InactiveState extends SensorState {
  canSendMeasurement() {
    return false;
  }

  getName() {
    return "INACTIVE";
  }
}

module.exports = InactiveState;
const SensorState = require("./SensorState");

class FaultyState extends SensorState {
  canSendMeasurement() {
    return false;
  }

  getName() {
    return "FAULTY";
  }
}

module.exports = FaultyState;